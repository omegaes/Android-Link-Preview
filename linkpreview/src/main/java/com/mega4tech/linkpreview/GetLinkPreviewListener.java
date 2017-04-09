package com.mega4tech.linkpreview;

/**
 * Created by aboodba on 09/04/2017.
 */

public interface GetLinkPreviewListener {
    void onSuccess(LinkPreview link);
    void onFailed(Exception e);
}
