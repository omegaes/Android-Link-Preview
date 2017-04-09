package com.mega4tech.linkpreview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by aboodba on 09/04/2017.
 */

public class LinkUtil {


    private static LinkUtil instance;

    public static LinkUtil getInstance() {
        if (instance == null)
            instance = new LinkUtil();
        return instance;
    }

    private LinkUtil() {

    }

    public void getLinkPreview(final Context context, @NonNull final String url, final  GetLinkPreviewListener listener) {

        if (TextUtils.isEmpty(url)) {
            if (listener != null)
                listener.onFailed(new IllegalArgumentException("URL should not be null or empty"));
            return;
        }

        try {
            validateURL(url);
        } catch (URISyntaxException e) {
            if (listener != null)
                listener.onFailed(e);
        } catch (MalformedURLException e) {
            if (listener != null)
                listener.onFailed(e);
        }


        final OkHttpClient client = new OkHttpClient();
        try {

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException throwable) {
                    if (listener != null)
                        listener.onFailed(throwable);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        if (listener != null)
                            listener.onFailed(new IOException("Unexpected code " + response));
                        return;
                    }

                    String imageLink, title = null, description = null, link = null, siteName = null;
                    File file = null;
                    Elements titleElements;
                    Elements descriptionElements;
                    Elements imageElements;
                    Elements siteElements;
                    Elements linkElements;

                    String site = "";
                    Document doc = null;
                    doc = Jsoup.parse(response.body().string());
                    titleElements = doc.select("title");
                    descriptionElements = doc.select("meta[name=description]");

                    if (url.contains("bhphotovideo")) {
                        imageElements = doc.select("image[id=mainImage]");
                        site = "bhphotovideo";
                    } else if (url.contains("www.amazon.com/gp/aw/d")) {
                        imageElements = doc.select("image[id=mainImage]");
                        site = "www.amazon.com/gp/aw/d";
                    } else if (url.contains("www.amazon.com/")) {
                        imageElements = doc.select("imageFile[data-old-hires]");
                        site = "www.amazon.com/";
                    } else if (url.contains("m.clove.co.uk")) {
                        imageElements = doc.select("imageFile[id]");
                        site = "m.clove.co.uk";
                    } else if (url.contains("www.clove.co.uk")) {
                        imageElements = doc.select("li[data-thumbnail-path]");
                        site = "www.clove.co.uk";
                    } else
                        imageElements = doc.select("meta[property=og:image]");


                    imageLink = getImageLink(imageElements, site);
                    siteElements = doc.select("meta[property=og:site_name]");
                    linkElements = doc.select("meta[property=og:url]");

                    if (titleElements != null && titleElements.size() > 0) {
                        title = titleElements.get(0).text();
                    }
                    if (descriptionElements != null && descriptionElements.size() > 0) {
                        description = descriptionElements.get(0).attr("content");
                    }
                    if (linkElements != null && linkElements.size() > 0) {
                        link = linkElements.get(0).attr("content");
                    } else {
                        linkElements = doc.select("link[rel=canonical]");
                        if (linkElements != null && linkElements.size() > 0) {
                            link = linkElements.get(0).attr("href");
                        }
                    }
                    if (siteElements != null && siteElements.size() > 0) {
                        siteName = siteElements.get(0).attr("content");
                    }


                    if (imageLink != null) {
                        Request request = new Request.Builder()
                                .url(imageLink)
                                .build();
                        Response response1 = null;
                        response1 = client.newCall(request).execute();
                        if (response1.isSuccessful()) {
                            file = new File(context.getCacheDir(), System.currentTimeMillis() + "");
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(response1.body().bytes());
                            fos.close();
                            response1.body().close();
                        }
                    }

                    LinkPreview linkPreview = new LinkPreview();
                    linkPreview.setDescription(description);
                    linkPreview.setLink(link);
                    linkPreview.setSiteName(siteName);
                    linkPreview.setImageFile(file);
                    linkPreview.setTitle(title);

                    if (listener != null)
                        listener.onSuccess(linkPreview);

                }
            });
        } catch (Exception ex) {
            if (listener != null)
                listener.onFailed(ex);
        }


    }


    private String getImageLink(Elements elements, String site) {
        String imageLink = null;
        if (elements != null && elements.size() > 0) {
            switch (site) {
                case "www.amazon.com/":
                    imageLink = elements.get(0).attr("data-old-hires");
                    break;

                case "m.clove.co.uk":
                case "bhphotovideo":
                    imageLink = elements.get(0).attr("src");
                    break;


                case "www.clove.co.uk":
                    imageLink = "https://www.clove.co.uk" + elements.get(0).attr("data-thumbnail-path");
                    break;
                default:
                    imageLink = elements.get(0).attr("content");
                    break;
            }

        }
        return imageLink;
    }


    public boolean validateURL(String url) throws URISyntaxException, MalformedURLException {
        URL u = null;
        u = new URL(url);
        u.toURI();
        return true;
    }

}
