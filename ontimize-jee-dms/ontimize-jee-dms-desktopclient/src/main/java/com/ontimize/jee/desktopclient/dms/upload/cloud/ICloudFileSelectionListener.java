package com.ontimize.jee.desktopclient.dms.upload.cloud;

import java.io.Serializable;

public interface ICloudFileSelectionListener<T> extends Serializable {

	void onFileSelected(T file);

}
