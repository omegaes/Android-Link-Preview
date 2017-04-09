# Android Link Preview

An Android Library with demo application, to fetch meta-data from url, like Facebook and Whatsapp ....

## Installation

Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add the dependency
```
    dependencies {
    	compile 'com.github.omegaes:Android-Link-Preview:1.0.1'
    }
````


## Usage

Request metadata for url, it will execute in background thread, you will get an instance from LinkPreview
or an exception, keep in your mind that onSuccess, onFailed execute in background thread, use handler to access UI objects

```
LinkUtil.getInstance().getLinkPreview(Context, String url,GetLinkPreviewListener)
```

LinkPreview

```
class LinkPreview {

    String title;
    String description;
    String link;
    String siteName;
    File imageFile;

}
```

GetLinkPreviewListener
```
interface {
    void onSuccess(LinkPreview link);
    void onFailed(Exception e);
}
```


## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

## History

Not yet

## Author

* **Abdulrahman Babil** - *Software engineer* - [Mega4Tech](http://mega4tech.com)

