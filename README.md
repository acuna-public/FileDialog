## FileDialog
FileDialog is a pretty file selection dialog for Android

![screenshot](https://github.com/acuna-public/FileDialog/blob/master/screenshot.png?raw=true)

**Advantages**

- Material Design
- Supports Android 4.0+
- Cloud storages support (Use [Storager](https://github.com/acuna-public/Storager) library for this)
- Themes support
<br>

**Usage**

~~~java
FileDialog dialog = new FileDialog (MainActivity.this);

dialog.setTitle (R.string.dialog_title);      // Dialog title
dialog.setStorage (storage);                  // Set storage (optional). Default is Android file system
dialog.setRootPath ("path");                  // Root path
dialog.setFileContent ("123");                // Write this content to file (when FileSaveListener () callback called) 
dialog.setStyle (R.style.Dialog);             // Dialog theme style (optional)
dialog.setListener (listener)                 // Set listener (see manual below)
dialog.setFileType ("jpg", "txt");            // Files extensions to show in file list
dialog.setShowType (FileDialog.Type.ALL);     // Files show types: FILES, FOLDERS, ALL
dialog.show ();                               // Do not forget to call this to draw the dialog!
~~~

Callbacks:

On file select:

~~~java
dialog.setListener (new FileDialog.SelectFileListener () {
  
  @Override
  public void onSelect (Item file, DialogInterface dialog) {
    
    dialog.dismiss ();
    
    Toast.makeText (getApplicationContext (), String.format ("File %f was selected", file.toString ()), Toast.LENGTH_SHORT).show ();
    
  }
  
  @Override
  public void onError (Exception e) {
    Toast.makeText (getApplicationContext (), e.getMessage (), Toast.LENGTH_SHORT).show ();
  }
  
});
~~~

On folder select and submit:

~~~java
dialog.setListener (new FileDialog.FolderListener () {
  
  @Override
  public String onSubmit (Item folder) { // Pressed "OK" button
    return file.read ();
  }
  
  @Override
  public void onSelect (Item file) {
    Toast.makeText (getApplicationContext (), String.format ((file.isDir () ? "Folder" : "File") + " %f was selected", file.toString ()), Toast.LENGTH_SHORT).show ();
  }
  
  @Override
  public void onCreateDir (Item folder) {
    Toast.makeText (getApplicationContext (), String.format ("Folder %f was created", file.toString ()), Toast.LENGTH_SHORT).show ();
  }
  
  @Override
  public void onError (Exception e) {
    Toast.makeText (getApplicationContext (), e.getMessage (), Toast.LENGTH_SHORT).show ();
  }
  
});
~~~

On file open:

~~~java
dialog.setListener (new FileDialog.FileOpenListener () {
  
  @Override
  public String onOpen (Item file) {
    return file.read ();
  }
  
  @Override
  public void onSave (Item file, String content) {
    Toast.makeText (getApplicationContext (), String.format ("File %f was saved", file.toString ()), Toast.LENGTH_SHORT).show ();
  }
  
  @Override
  public void onCreateDir (Item file) {
    Toast.makeText (getApplicationContext (), String.format ("Folder %f was created", file.toString ()), Toast.LENGTH_SHORT).show ();
  }
  
  @Override
  public void onSelect (Item file) {
    Toast.makeText (getApplicationContext (), String.format ("Folder %f was selected", file.toString ()), Toast.LENGTH_SHORT).show ();
  }
  
  @Override
  public void onError (Exception e) {
    Toast.makeText (getApplicationContext (), e.getMessage (), Toast.LENGTH_SHORT).show ();
  }
  
});
~~~

On file save

~~~java
dialog.setListener (new FileDialog.FileSaveListener () {
  
  @Override
  public void onSave (Item file, String content) {
    Toast.makeText (getApplicationContext (), String.format ("File %f was saved", file.toString ()), Toast.LENGTH_SHORT).show ();
  }
  
  @Override
  public void onCreateDir (Item folder) {
    Toast.makeText (getApplicationContext (), String.format ("Folder %f was created", file.toString ()), Toast.LENGTH_SHORT).show ();
  }
  
  @Override
  public void onSelect (Item file, DialogInterface dialog) {
    Toast.makeText (getApplicationContext (), String.format ("Folder %f was selected", file.toString ()), Toast.LENGTH_SHORT).show ();
  }
  
  @Override
  public void onError (Exception e) {
    Toast.makeText (getApplicationContext (), e.getMessage (), Toast.LENGTH_SHORT).show ();
  }
  
});
~~~
