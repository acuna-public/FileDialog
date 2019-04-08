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
    
On file select:
    
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
    
On folder select and submit:
    
    dialog.setListener (new FileDialog.FolderListener () {
        
        @Override
        public String onSubmit (Item folder) { // Press OK button
            return file.read ();
        }
		
        @Override
        public void onSelect (Item file) {
            Toast.makeText (getApplicationContext (), String.format ((file.isDir () ? "Folder" : "File") + " %f was selected", file.toString ()), Toast.LENGTH_SHORT).show ();
        }

        @Override
        public void onCreateDir (Item file) {
            Toast.makeText (getApplicationContext (), String.format ("Folder %f was created", file.toString ()), Toast.LENGTH_SHORT).show ();
        }
        
        @Override
        public void onError (Exception e) {
            Toast.makeText (getApplicationContext (), e.getMessage (), Toast.LENGTH_SHORT).show ();
        }

    });
    
On file open:
    
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
        public void onSelect (Item file, DialogInterface dialog) {
            Toast.makeText (getApplicationContext (), String.format ("Folder %f was selected", file.toString ()), Toast.LENGTH_SHORT).show ();
        }

        @Override
        public void onError (Exception e) {
            Toast.makeText (getApplicationContext (), e.getMessage (), Toast.LENGTH_SHORT).show ();
        }

    });
    
On file save
    
    dialog.setListener (new FileDialog.FileSaveListener () {

        @Override
        public void onSave (Item file, String content) {
            Toast.makeText (getApplicationContext (), String.format ("File %f was saved", file.toString ()), Toast.LENGTH_SHORT).show ();
        }

        @Override
        public void onCreateDir (Item file) {
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
    
Do not forget to call `show ()` to draw the dialog:

    dialog.show ();
