package com.hitomi.tilibrary.view.video;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.LoopingMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;

import com.hitomi.tilibrary.view.video.source.ExoSourceManager;

import java.io.File;

/**
 * Created by Vans Z on 2020/5/19.
 */
@UnstableApi
public class ExoVideoView extends AdaptiveTextureView {
    private static final String TAG = "ExoVideoView";
    public static final String CACHE_DIR = "TransExo";

    private String url;
    private boolean requestLayout;
    private boolean invalidate;
    private ExoPlayer exoPlayer;
    private ExoSourceManager exoSourceManager;
    private File cacheFile;
    private VideoStateChangeListener videoStateChangeListener;

    public ExoVideoView(@NonNull Context context) {
        this(context, null);
    }

    public ExoVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExoVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAlpha(0); // 初始化置为透明是为了防止自适应宽高而出现的一次闪屏问题
        cacheFile = getCacheDir();
        exoSourceManager = ExoSourceManager.newInstance(context, null);
        newExoPlayer(context);
    }

    private void newExoPlayer(@NonNull Context context) {
        @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;
        DefaultRenderersFactory rendererFactory = new DefaultRenderersFactory(context);
        rendererFactory.setExtensionRendererMode(extensionRendererMode);
        exoPlayer = new ExoPlayer.Builder(context, rendererFactory)
                .setLooper(Looper.myLooper())
                .setTrackSelector(new DefaultTrackSelector(context))
                .setLoadControl(new DefaultLoadControl()).build();
        exoPlayer.setVideoTextureView(this);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onVideoSizeChanged(VideoSize videoSize) {
                if (currentVideoWidth != videoSize.width && currentVideoHeight != videoSize.height) {
                    Log.e(TAG, "ExoVideoView.invoke()");
                    currentVideoWidth = videoSize.width;
                    currentVideoHeight = videoSize.height;
                    requestLayout();
                    requestLayout = true;
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (Player.STATE_BUFFERING == playbackState) { // 缓冲中
                    if (videoStateChangeListener != null)
                        videoStateChangeListener.onVideoBuffering();
                } else if (Player.STATE_READY == playbackState) { // 缓冲结束，可以播放
                    if (videoStateChangeListener != null)
                        videoStateChangeListener.onVideoReady();
                }
            }
        });
        invalidate = false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (requestLayout) { // 在视频尺寸自适应确定后取消透明
            requestLayout = false;
            Log.e(TAG, "ExoVideoView.onVideoRendered()");
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAlpha(1);
                    if (videoStateChangeListener != null)
                        videoStateChangeListener.onVideoRendered();
                }
            }, 15);
            setAlpha(1);
        }
    }

    private File getCacheDir() {
        File cacheDir = new File(getContext().getCacheDir(), CACHE_DIR);
        if (!cacheDir.exists()) cacheDir.mkdirs();
        return cacheDir;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    public void setSource(String url, boolean autoPlay) {
        this.url = url;
        if (!exoPlayer.isLoading()) {
            MediaSource videoSource =
                    exoSourceManager.getMediaSource(url, true, true, true, cacheFile, null);
            exoPlayer.prepare(new LoopingMediaSource(videoSource));
        }
        exoPlayer.setPlayWhenReady(autoPlay);
    }

    public void play() {
        if (invalidate) {
            newExoPlayer(getContext());
            setSource(url, true);
        } else {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public void pause() {
        exoPlayer.setPlayWhenReady(false);
    }

    public void resume() {
        exoPlayer.setPlayWhenReady(true);
    }

    public void reset() {
        exoPlayer.seekTo(0);
        exoPlayer.setPlayWhenReady(false);
    }

    public void destroy() {
        invalidate = true;
        exoPlayer.release();
    }

    public void setVideoStateChangeListener(VideoStateChangeListener listener) {
        videoStateChangeListener = listener;
    }

    public interface VideoStateChangeListener {
        /**
         * 视频正在缓冲
         */
        void onVideoBuffering();

        /**
         * 视频缓冲完毕，可以开始播放
         */
        void onVideoReady();

        /**
         * 视频渲染完毕，第一帧图像已经显示出来
         */
        void onVideoRendered();
    }
}
