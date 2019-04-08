# FileDialog
FileDialog is a handy file selection dialog for Android.<br>
<br>
**Usage**

    FileDialog dialog = new FileDialog (MainActivity.this);
    
    dialog.setTitle (R.string.dialog_title);      // Dialog title
    dialog.setStorage (storage);                  // Set storage (optional). Default is Android file system
    dialog.setRootPath ("path");                  // Root path
    dialog.setStyle (R.style.Dialog);             // Dialog theme style (optional)
    dialog.setFileType ("jpg", "txt");            // Files extensions to show in file list
    dialog.setShowType (FileDialog.Type.ALL);     // Files show types: FILES, FOLDERS, ALL
    
Callbacks:
    
    dialog.setListener (new FileDialog.FileSaveListener () {

        @Override
        public void onSave (Item file, String content) {

            OS.alert (getApplicationContext (), getString (R.string.message_save_apps_success).replace ("%f", file.toString ()));
        }

        @Override
        public void onCreateDir (Item file) {}

        @Override
        public void onSelect (Item file, DialogInterface dialog) {}

        @Override
        public void onError (Exception e) {

            OS.alert (getApplicationContext (), e);
        }

    });

    dialog.show (); // Show dialog (necessary)
