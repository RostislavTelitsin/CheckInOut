# CheckInOut
This is android/java tool to implement CheckIn and CheckOut on site of my employer (when you start and finish your working day). At the very begining it was desktop application but now it's android app

Json format is used to save username and password in file:
~~~
  File txtFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "checkInOutData.txt");
  FileInputStream fis = new FileInputStream(txtFile);
  InputStreamReader isr = new InputStreamReader(fis);
  BufferedReader buf = new BufferedReader(isr);
  String lines;
  while ((lines = buf.readLine()) != null) {
      sb.append(lines);
  }
  fis.close();
  PassData upload_data = up_gson.fromJson(String.valueOf(sb), PassData.class);


  userdata = sb.toString().split("\"");
  usr.setText(upload_data.userName);
  pwd.setText(upload_data.pass);
~~~

Apache httpclien lib is used for checkin serrver access:
~~~
 HttpPost httpPost_checkin = new HttpPost(url_checkin);
 List<NameValuePair> checkinparams = new ArrayList<NameValuePair>();
 checkinparams.add(new BasicNameValuePair("roarand", roarand));
 checkinparams.add(new BasicNameValuePair("serviceId", "attendance_web_check_in_out"));

 UrlEncodedFormEntity entity_checkin = new UrlEncodedFormEntity(checkinparams, StandardCharsets.UTF_8);
 httpPost_checkin.setEntity(entity_checkin);

 ClassicHttpResponse httpResponse_checkin = (ClassicHttpResponse)httpClient.execute(httpPost_checkin);
 bodyN = EntityUtils.toString(httpResponse_checkin.getEntity());
~~~
