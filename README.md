# FileDialog
FileDialog is a handy file selection dialog for Android.<br>
<br>
**Usage**

    FileDialog dialog = new FileDialog (MainActivity.this);
    
    dialog.setStyle (fapman.andro.dialogStyle ());
    dialog.setLayout (R.layout.file_dialog_item);
    dialog.setTitle (R.string.title_apps_folder);
    //dialog.setStorage (fapman.storagerInit ());
    dialog.setRootPath (OS.extStorageDir ());
    dialog.setShowType (FileDialog.Type.ALL);
