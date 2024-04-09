package com.hitomi.tilibrary.view.video.source;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.CacheKeyFactory;
import androidx.media3.datasource.cache.CacheSpan;
import androidx.media3.datasource.cache.ContentMetadata;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.dash.DefaultDashChunkSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.smoothstreaming.DefaultSsChunkSource;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
import androidx.media3.exoplayer.source.LoopingMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;

@UnstableApi
public class ExoSourceManager {

    private static final String TAG = "ExoSourceManager";
    private static final long DEFAULT_MAX_SIZE = 512 * 1024 * 1024;
    private static final int TYPE_RTMP = 4;
    private static Cache mCache;
    /**
     * 忽律Https证书校验
     */
    private static boolean mSkipSSLChain = false;

    private Context mAppContext;

    private Map<String, String> mMapHeadData;

    private String mDataSource;

    private boolean isCached = false;

    public static ExoSourceManager newInstance(Context context, @Nullable Map<String, String> mapHeadData) {
        return new ExoSourceManager(context, mapHeadData);
    }

    private ExoSourceManager(Context context, Map<String, String> mapHeadData) {
        mAppContext = context.getApplicationContext();
        mMapHeadData = mapHeadData;

    }


    /**
     * @param dataSource  链接
     * @param preview     是否带上header，默认有header自动设置为true
     * @param cacheEnable 是否需要缓存
     * @param isLooping   是否循环
     * @param cacheDir    自定义缓存目录
     */
    public MediaSource getMediaSource(String dataSource, boolean preview, boolean cacheEnable, boolean isLooping, File cacheDir, @Nullable String overrideExtension) {
        MediaSource mediaSource = null;
        mDataSource = dataSource;
        Uri contentUri = Uri.parse(dataSource);
        int contentType = inferContentType(dataSource, overrideExtension);
        switch (contentType) {
            case C.CONTENT_TYPE_SS:
                mediaSource = new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(getDataSourceFactoryCache(mAppContext, cacheEnable, preview, cacheDir)),
                        new DefaultDataSourceFactory(mAppContext, null,
                                getHttpDataSourceFactory(mAppContext, preview)))
                        .createMediaSource(MediaItem.fromUri(contentUri));
                break;
            case C.CONTENT_TYPE_DASH:
                mediaSource = new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(getDataSourceFactoryCache(mAppContext, cacheEnable, preview, cacheDir)),
                        new DefaultDataSourceFactory(mAppContext, null,
                                getHttpDataSourceFactory(mAppContext, preview)))
                        .createMediaSource(MediaItem.fromUri(contentUri));
                break;
            case C.CONTENT_TYPE_HLS:
                mediaSource = new HlsMediaSource.Factory(getDataSourceFactoryCache(mAppContext, cacheEnable, preview, cacheDir))
                        .createMediaSource(MediaItem.fromUri(contentUri));
                break;
            /*case TYPE_RTMP:
                RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory(null);
                mediaSource = new ProgressiveMediaSource.Factory(rtmpDataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(contentUri));

//                mediaSource = new ExtractorMediaSource.Factory(rtmpDataSourceFactory)
//                        .setExtractorsFactory(new DefaultExtractorsFactory())
//                        .createMediaSource(contentUri);
                break;*/
            case C.CONTENT_TYPE_OTHER:
            default:
                mediaSource = new ProgressiveMediaSource.Factory(getDataSourceFactoryCache(mAppContext, cacheEnable, preview, cacheDir))
                        .createMediaSource(MediaItem.fromUri(contentUri));
                break;
        }
        if (isLooping) {
            return new LoopingMediaSource(mediaSource);
        }
        return mediaSource;
    }


    @SuppressLint("WrongConstant")
    @C.ContentType
    public static int inferContentType(String fileName, @Nullable String overrideExtension) {
        fileName = fileName.toLowerCase(Locale.ROOT);//Util.toLowerInvariant();
        if (fileName.startsWith("rtmp:")) {
            return TYPE_RTMP;
        } else {
            return inferContentType(Uri.parse(fileName), overrideExtension);
        }
    }

    @C.ContentType
    public static int inferContentType(Uri uri, @Nullable String overrideExtension) {
        return Util.inferContentType(uri, overrideExtension);
    }

    /**
     * 本地缓存目录
     */
    public static synchronized Cache getCacheSingleInstance(Context context, File cacheDir) {
        String dirs = context.getCacheDir().getAbsolutePath();
        if (cacheDir != null) {
            dirs = cacheDir.getAbsolutePath();
        }
        if (mCache == null) {
            String path = dirs + File.separator + "exo";
            boolean isLocked = SimpleCache.isCacheFolderLocked(new File(path));
            if (!isLocked) {
                mCache = new SimpleCache(new File(path), new LeastRecentlyUsedCacheEvictor(DEFAULT_MAX_SIZE));
            }
        }
        return mCache;
    }

    public void release() {
        isCached = false;
        if (mCache != null) {
            try {
                mCache.release();
                mCache = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cache 需要 release 之后才能 clear
     *
     * @param cacheDir 缓存文件夹
     * @param url      如果 url 值是 null, 表示清楚所有缓存
     */
    public static void clearCache(Context context, File cacheDir, String url) {
        try {
            Cache cache = getCacheSingleInstance(context, cacheDir);
            if (!TextUtils.isEmpty(url)) {
                if (cache != null) {
                    final CacheKeyFactory factory = CacheKeyFactory.DEFAULT;
                    final String key = factory.buildCacheKey(new DataSpec.Builder()
                            .setUri(Uri.parse(url))
                            .build());
                    cache.removeResource(key);
                }
            } else {
                if (cache != null) {
                    for (String key : cache.getKeys()) {
                        cache.removeResource(key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean cachePreView(Context context, File cacheDir, String url) {
        return resolveCacheState(getCacheSingleInstance(context, cacheDir), url);
    }

    public boolean hadCached() {
        return isCached;
    }


    public static boolean isSkipSSLChain() {
        return mSkipSSLChain;
    }

    /**
     * 设置https忽略证书
     *
     * @param skipSSLChain true时是hulve
     */
    public static void setSkipSSLChain(boolean skipSSLChain) {
        mSkipSSLChain = skipSSLChain;
    }

    /**
     * 获取SourceFactory，是否带Cache
     */
    private DataSource.Factory getDataSourceFactoryCache(Context context, boolean cacheEnable, boolean preview, File cacheDir) {
        if (cacheEnable) {
            Cache cache = getCacheSingleInstance(context, cacheDir);
            if (cache != null) {
                isCached = resolveCacheState(cache, mDataSource);
                CacheDataSource.Factory cacheFactory = new CacheDataSource.Factory();
                cacheFactory.setCache(cache);
                cacheFactory.setUpstreamDataSourceFactory(getDataSourceFactory(context, preview));
                cacheFactory.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
                return cacheFactory;
                //new CacheDataSourceFactory(cache, getDataSourceFactory(context, preview), CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
            }
        }
        return getDataSourceFactory(context, preview);
    }

    /**
     * 获取SourceFactory
     */
    private DataSource.Factory getDataSourceFactory(Context context, boolean preview) {
        return new DefaultDataSourceFactory(context, preview ? null : DefaultBandwidthMeter.getSingletonInstance(context),
                getHttpDataSourceFactory(context, preview));
    }

    private DataSource.Factory getHttpDataSourceFactory(Context context, boolean preview) {
        boolean allowCrossProtocolRedirects = false;
        if (mMapHeadData != null && mMapHeadData.size() > 0) {
            allowCrossProtocolRedirects = "true".equals(mMapHeadData.get("allowCrossProtocolRedirects"));
        }
        if (mSkipSSLChain) {
            ExoHttpDataSourceFactory dataSourceFactory = new ExoHttpDataSourceFactory(Util.getUserAgent(context,
                    TAG), preview ? null : DefaultBandwidthMeter.getSingletonInstance(context), ExoHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    ExoHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, allowCrossProtocolRedirects);

            if (mMapHeadData != null && !mMapHeadData.isEmpty()) {
                dataSourceFactory.setDefaultRequestProperties(mMapHeadData);
            }
            return dataSourceFactory;
        }
        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        dataSourceFactory.setUserAgent(Util.getUserAgent(context, TAG));
        dataSourceFactory.setConnectTimeoutMs(ExoHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
        dataSourceFactory.setReadTimeoutMs(ExoHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS);
        dataSourceFactory.setAllowCrossProtocolRedirects(allowCrossProtocolRedirects);
        dataSourceFactory.setTransferListener(preview ? null : DefaultBandwidthMeter.getSingletonInstance(context));
        if (mMapHeadData != null && !mMapHeadData.isEmpty()) {
            dataSourceFactory.setDefaultRequestProperties(mMapHeadData);
        }
        return dataSourceFactory;
    }

    /**
     * 根据缓存块判断是否缓存成功
     *
     * @param cache
     */
    public static boolean resolveCacheState(Cache cache, String url) {
        boolean isCache = true;
        if (!TextUtils.isEmpty(url)) {
            final CacheKeyFactory factory = CacheKeyFactory.DEFAULT;
            final String key = factory.buildCacheKey(new DataSpec.Builder()
                    .setUri(Uri.parse(url))
                    .build());
            if (!TextUtils.isEmpty(key)) {
                NavigableSet<CacheSpan> cachedSpans = cache.getCachedSpans(key);
                if (cachedSpans.size() == 0) {
                    isCache = false;
                } else {
                    long contentLength = cache.getContentMetadata(key).get(ContentMetadata.KEY_CONTENT_LENGTH, C.LENGTH_UNSET);
                    long currentLength = 0;
                    for (CacheSpan cachedSpan : cachedSpans) {
                        currentLength += cache.getCachedLength(key, cachedSpan.position, cachedSpan.length);
                    }
                    isCache = currentLength >= contentLength;
                }
            } else {
                isCache = false;
            }
        }
        return isCache;
    }
}
