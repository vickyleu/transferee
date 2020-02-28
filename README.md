# Transferee
transferee 可以帮助你完成从缩略图到原图的无缝过渡转变，功能体系仿照并涵盖 ios 版本的 QQ、微信朋友圈、新浪微博的图片浏览功能。

支持的功能:

+ 缩略图到大图或者大图到缩略图的无缝过渡动画
+ 无缩略图信息时，自动改变动画的行为为平移过渡->加载大图->伸展图片动画
+ 傻瓜式绑定 RecyclerView / ListView / GridView / ImageView
+ 支持预览图片缩放，拖动，旋转等手势操作
+ 支持自定义图片加载器[目前已经有 UniversalImageLoader 和 GlideImageLoader]
+ 支持自定义图片索引指示器
+ 支持自定义图片加载进度条
+ 支持图片保存
+ 支持大长图预览
+ 支持 Gif 图预览
+ 支持拖拽关闭

如有任何问题可以提 Issues

# Preview
<img src="preview/transferee.gif" />

# Sample

[demo.apk](https://github.com/Hitomis/transferee/tree/master/preview/app-release.apk)


# Dependency
Gradle 依赖，之前版本已作废，待现在版本稳定后，再上传

# Usage

step 1: 一个页面只创建一个 transferee 示例 (建议写在 onCreate 方法中)
```
transferee = Transferee.getDefault(context);
```

setp 2: 为 transferee 创建参数配置器，一般配置固定不变的参数
```
TransferConfig config = TransferConfig.build()
       .setSourceImageList(ImageConfig.getSourcePicUrlList()) // 图片url集合
       .setMissPlaceHolder(R.mipmap.ic_empty_photo) // 图片加载前的占位图
       .setErrorPlaceHolder(R.mipmap.ic_empty_photo) // 图片加载错误后的占位图
       .setProgressIndicator(new ProgressPieIndicator()) // 图片加载进度指示器
       .setIndexIndicator(new NumberIndexIndicator()) // 图片数量索引指示器
       .setImageLoader(GlideImageLoader.with(getApplicationContext())) // 设置图片加载器
       .setJustLoadHitImage(true) // 是否只加载当前显示在屏幕中的的图片
       .enableDragClose(true) // 开启拖拽关闭
       .setOnLongClcikListener(new Transferee.OnTransfereeLongClickListener() {
           @Override
           public void onLongClick(ImageView imageView, int pos) {
               saveImageByUniversal(imageView);
           }
       })
       .bindRecyclerView(gvImages, R.id.iv_thum);
       
TransferConfig 可以绑定 ImageView, ListView, RecyclerView, 详见下面 api 说明
                                     
```

setp 3: 显示 transferee
```
config.setNowThumbnailIndex(position);
transferee.apply(config).show();
```

# Config
| 属性 | 说明 |
| :--: | :--: |
| nowThumbnailIndex | 缩略图在图组中的索引 |
| offscreenPageLimit | 显示 transferee 时初始化加载的图片数量, 默认为1, 表示第一次加载3张(nowThumbnailIndex, nowThumbnailIndex + 1, nowThumbnailIndex - 1); 值为 2, 表示加载5张。依次类推 |
| missPlaceHolder | 缺省的占位图，资源 id 格式。图片未加载完成时默认显示的图片 |
| missDrawable | 缺省的占位图，Drawable 格式。图片未加载完成时默认显示的图片 |
| errorPlaceHolder | 加载错误的占位图，资源 id 格式。原图加载错误时显示的图片 |
| errorDrawable | 加载错误的占位图，Drawable 格式。原图加载错误时显示的图片 |
| backgroundColor | transferee 显示时，图片后的背景色 |
| duration | transferee 播放过渡动画的动画时长 |
| justLoadHitImage | 是否只加载当前显示在屏幕中的的原图。如果设置为 true，那么只有当 transferee 切换到当前页面时，才会触发当前页面原图的加载动作，否则按 offscreenPageLimit 所设置的数值去做预加载和当前页面的加载动作 |
| enableDragClose | 是否支持向下拖拽关闭 |
| sourceImageList | 原图路径集合 |
| progressIndicat | 图片加载进度指示器 (默认内置 ProgressPieIndicator 和 ProgressBarIndicator)。可实现 IProgressIndicator 接口定义自己的图片加载进度指示器 |
| indexIndicator | 图片索引指示器 (默认内置 CircleIndexIndicator 和 NumberIndexIndicator)。可实现 IIndexIndicator 接口定义自己的图片索引指示器 |
| imageLoader | 图片加载器 (默认 UniversalImageLoader )。可实现 ImageLoader 接口定义自己的图片加载器 |
| imageId | RecyclerView 或者 ListView 的 ItemView 中的 ImageView id|
| listView | 如果你是使用的 ListView 或者 GridView 来排列显示图片，那么需要将你的 ListView 或者 GridView 传入 bindListView() 方法中 |
| recyclerView | 如果你使用的 RecyclerView 来排列显示图片，需要将 RecyclerView 传入 bindRecyclerView() 方法中 |
| imageView | 如果只想对单个 ImageView 使用此库的功能，或者界面上单个的 ImageView 是相册的封面，那么使用 bindImageView(...) 或者它的重载方法可以满足你的需求  |


# Method
| 方法 | 说明 |
| :--: | :--: |
| getDefault(context) | 获取 transferee 实例 |
| apply(config) | 将配置参数对象应用到 transferee 实例中 |
| show() | 打开/显示 transferee |
| show(listener) | 打开/显示 transferee，并监听显示/关闭状态 |
| isShown() | transferee 是否显示 |
| dismiss() | 关闭 transferee |
| clear(imageLoader) | 清除 ImageLoader 中加载的缓存 |
| setOnTransfereeStateChangeListener(listener) | 设置 transferee 显示/关闭状态改变的监听器 |

# Update log
+ v0.5
  - 优化打开或者关闭 transferee 时背景色显示效果，从直接显示背景色优化为渐变显示。
  - 基于 [#26](https://github.com/Hitomis/transferee/issues/26) 添加 transferee 使用本地图片场景下的 Demo
  - 为 transferee 组件添加背景色自定义功能
  - 为 transferee 组件添加长按监听器，并添加了长按保存图片到相册的示例代码
  - 更新了部分示例代码中失效的图片地址

+ v1.0.0
  - 将项目托管到 jitpack。目前可以使用添加 dependencies 的方式，导入 transferee 组件
  
+ v1.1.0
  - 修复 transferee 单例为普通实例，解决多个界面公用 transferee 单例时的异常问题

+ v1.2.0
  - 针对之前版本的使用复杂考虑，添加了直接绑定 ListView，GridView，RecyclerView 即可使用，不再需要人肉传入 originImageList
  - 修复有超出屏幕外图片情境下 transferee 崩溃的情景
  - 修复 Issues 中各位同学反馈的bug
  
+ v1.3.0
  - 支持 transferee 绑定单个 ImageView 直接使用
  - 支持 transferee 绑定单个 ImageView 后多样化展示图片，兼容有缩略图，无缩略图，或者实际图片数量远大于这里的一个 ImageView 数量，例如点击相册封面图片，可以查看相册中其他的图片的的功能
  - 优化 api 使用方式，最简可以只需要绑定控件，传入图片地址后就能使用
  - 优化用户体验，不再需要关注缩略图是否加载完成，任何时刻都可以立即打开 transferee
  - 修复内存泄漏问题
  - 简化 demo 代码

# Todo
+ [x] 支持图片拖拽关闭
+ [x] 支持 gif 图片 

# Licence
    Copyright 2017 Hitomis, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 


