package net.phazecraft.tools;

import java.io.File;

import net.phazecraft.log.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

public class UploadFile {
	@SuppressWarnings("deprecation")
	public static void sendFile(String scriptPath, String fileName, File file) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(scriptPath);

		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new FileBody(file, "image/png");
		mpEntity.addPart("userfile", cbFile);

		httppost.setEntity(mpEntity);
		Logger.logInfo("executing request " + httppost.getRequestLine());
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();

		Logger.logInfo(response.getStatusLine().toString());
		if (resEntity != null) {
			Logger.logInfo(EntityUtils.toString(resEntity));
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}

		httpclient.getConnectionManager().shutdown();
	}
}