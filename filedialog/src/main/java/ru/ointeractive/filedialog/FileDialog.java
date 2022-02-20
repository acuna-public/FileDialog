  package ru.ointeractive.filedialog;
  /*
   Created by Acuna on 15.09.2016
  */
  
  import android.app.Activity;
  import android.app.AlertDialog;
  import android.app.ProgressDialog;
  import android.content.DialogInterface;
  import android.view.LayoutInflater;
  import android.view.View;
  import android.widget.EditText;
  
  import java.io.IOException;
  import java.net.URL;
  
  import ru.ointeractive.andromeda.OS;
  import ru.ointeractive.andromeda.AsyncTask;
  import ru.ointeractive.jabadaba.Files;
  import ru.ointeractive.jabadaba.Int;
  import ru.ointeractive.jstorage.Item;
  import ru.ointeractive.storage.Storage;
  import ru.ointeractive.jstorage.StorageException;
  import ru.ointeractive.jstorage.adapters.SDCard;
  import upl.core.Arrays;
  import upl.core.Log;
  import upl.core.exceptions.OutOfMemoryException;
  import upl.json.JSONException;
  import upl.json.JSONObject;
  import upl.util.ArrayList;
  import upl.util.List;

  public class FileDialog {
   
  	private Storage storage;
    static final String PARENT_DIR = "..";
    private int level = 0;
    private Item currentPath;
    private Provider provider;
    private Activity activity;
    private DialogInterface mDialog;
    private Type showType = Type.ALL;
    
    private FileSaveListener fileListener;
    private FileOpenListener fileReadListener;
    private FolderListener dirListener;
    private SelectFileListener selectFileListener;
    
    public interface FileSaveListener {
      
      void onSave (Item file, String content);
      void onSelect (Item file, DialogInterface dialog);
      void onCreateDir (Item folder);
      void onError (Exception e);
      
    }
    
    public interface FileOpenListener {
      
      String onOpen (Item file);
      void onSave (Item file, String content);
      void onSelect (Item file);
      void onCreateDir (Item folder);
      void onError (Exception e);
      
    }
    
    public interface FolderListener {
      
      void onSubmit (Item folder);
      void onSelect (Item file);
      void onCreateDir (Item folder);
      void onError (Exception e);
      
    }
    
    public interface SelectFileListener {
      
      void onSelect (Item file, DialogInterface dialog);
      void onError (Exception e);
      
    }
    
    public enum SelectionType {
      
      MULTIPLE,
      
    }
    
    private String rootPath = "";
    
    public FileDialog setRootPath (String rootPath) {
      
      if (rootPath != null && !rootPath.equals (""))
        this.rootPath = rootPath;
      
      return this;
      
    }
    
    private String url;
    
    public FileDialog setURL (String url) {
      
      this.url = url;
      return this;
      
    }
    
    public enum Type { FOLDERS, ALL }
    
    private int title = 0;
    
    public FileDialog setTitle (int title) {
      
      this.title = title;
      return this;
      
    }
    
    public FileDialog setShowType (Type type) {
      
      showType = type;
      return this;
      
    }
    
    public FileDialog setStorage (Storage storage) {
	
	    this.storage = storage;
      return this;
      
    }
    
	  public FileDialog (Activity activity) {
      
      this.activity = activity;
      if (layout == -1) layout = R.layout.item;
      
    }
    
    /*public FileDialog setIcons (Map<String, Integer> icons) {

      this.icons = icons;
      return this;

    }*/
    
    public FileDialog setListener (FileSaveListener listener) {
      
      fileListener = listener;
      return this;
      
    }
    
    public FileDialog setListener (FileOpenListener listener) {
      
      fileReadListener = listener;
      return this;
      
    }
    
    public FileDialog setListener (FolderListener listener) {
      
      dirListener = listener;
      setShowType (Type.FOLDERS);
      
      return this;
      
    }
    
    public FileDialog setListener (SelectFileListener listener) {
      
      selectFileListener = listener;
      return this;
      
    }
    
    private class ShowItems extends AsyncTask<Void, Void, List<Provider>> {
      
      private ProgressDialog progress;
      private final List<Exception> errors = new ArrayList<> ();
      
      @Override
      protected void onPreExecute () {
        
        progress = ProgressDialog.show (activity, null, activity.getString (R.string.loading));
        progress.setCancelable (false);
        
      }
      
      @Override
      public List<Provider> doInBackground (Void... params) {
        
        List<Provider> files = new ArrayList<> ();
        
        try {
          
          if (storage == null)
            setStorage (new Storage (activity));
            
          if (storage.provider == null) {
          	
            SDCard sdcard = new SDCard ();
            
            JSONObject data = new JSONObject ();
            
            data.put ("folder", rootPath);
            
            JSONObject data2 = new JSONObject ();
            data2.put (sdcard.getName (), data);
            
	          storage.setConfigs (data2);
            storage.getProvider (sdcard.getName ());
            
          }
          
          if (currentPath == null) {
            
            currentPath = storage.setDir (rootPath);
            storage.makeDir (currentPath);
            
          }
          
          provider = new ru.ointeractive.filedialog.Files (currentPath);
          
          provider = provider.newInstance (currentPath);
          List<Provider> mFiles = provider.list ();
          
          if (!currentPath.getParent ().getShortFile ().equals (""))
            files.add (provider.newInstance (storage.setDir (PARENT_DIR)));
          
          if (Int.size (mFiles) > 0) {
            
            for (Provider file : mFiles) {
              
              if (
                (
                  showType != Type.FOLDERS && !file.item.isDir
                  && (
                    (Int.size (fileEndsWith) > 0 && Arrays.contains (Files.getExtension (file.toString ()), fileEndsWith))
                    ||
                    Int.size (fileEndsWith) == 0
                  ) && (
                    (Int.size (allowNames) > 0 && Arrays.contains (Files.getName (file.toString ()), allowNames))
                    ||
                    Int.size (allowNames) == 0
                  )
                ) || file.item.isDir
              ) {
                
                files.add (file);
                
                //if (allowNames.length > 0 && !file.isDir () && Arrays.contains (file.getName (), allowNames))
                //result = exists (fileChosen, dialog);
                
              }
              
            }
            
          }
          
        } catch (StorageException | OutOfMemoryException | JSONException e) {
          errors.add (e);
        }
        
        return files;
        
      }
      
      @Override
      public void onPostExecute (final List<Provider> files) {
        
        if (Int.size (errors) == 0) {
          
          AlertDialog.Builder builder = dialogBuilder ();
          
          if (title > 0 && level == 0)
            builder.setTitle (title);
          else
            builder.setTitle (currentPath.getShortFile ());
          
          if (selectFileListener == null || dirListener != null) {
            
            builder.setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener () {
              
              @Override
              public void onClick (DialogInterface dialog, int id) {}
              
            });
            
            if (selectFileListener == null)
              builder.setNeutralButton (R.string.button_new_dir, new DialogInterface.OnClickListener () {
                
                @Override
                public void onClick (DialogInterface dialog, int id) {}
                
              });
            
            builder.setNegativeButton (R.string.button_close, new DialogInterface.OnClickListener () {
              
              @Override
              public void onClick (DialogInterface dialog, int id) {}
              
            });
            
          }
	        
          builder.setAdapter (new FilesAdapter (activity, R.layout.dialog, layout, files), new DialogInterface.OnClickListener () {
            
            @Override
            public void onClick (final DialogInterface dialog, final int id) { // Кликнули по файлу (мы еще не знаем что это)
              
              mDialog = dialog;
              Item selected = files.get (id).item;
              
              boolean isParent = Files.getName (selected.getShortFile (), true).equals (PARENT_DIR);
              
              if (isParent)
	              currentPath = currentPath.getParent ();
              else
                currentPath = selected;
              
              if (currentPath.isDir || currentPath.getShortFile ().equals ("")) { // Открываем папку
                
                if (dirListener != null)
                  dirListener.onSelect (currentPath);
                else if (fileReadListener != null)
                  fileReadListener.onSelect (currentPath);
                else if (fileListener != null)
                  fileListener.onSelect (currentPath, dialog);
                
                level = 1;
                new ShowItems ().execute ();
                
              } else {
                
                if (fileReadListener != null) { // Кликаем по существующему файлу и спрашиваем нужно ли его переписать
                  
                  content = fileReadListener.onOpen (currentPath);
                  
                  new CreateFile ("file").execute ();
                  
                } else if (fileListener != null)
                  fileExists (currentPath, dialog);
                else if (dirListener != null)
                  dirListener.onSelect (currentPath);
                
                if (fileListener != null)
                  fileListener.onSelect (currentPath, dialog);
                else if (selectFileListener != null)
                  selectFileListener.onSelect (currentPath, dialog);
                
              }
              
            }
            
          });
          
          final AlertDialog dialog = builder.create ();
          
          dialog.show ();
          
          if (selectFileListener == null || dirListener != null) {
            
            dialog.getButton (AlertDialog.BUTTON_POSITIVE).setOnClickListener (new View.OnClickListener () {
              
              @Override
              public void onClick (View view) {
	
	              try {
		
		              if (dirListener != null || selectFileListener != null) {
			
			              dialog.dismiss ();
			
			              if (dirListener != null)
				              dirListener.onSubmit (currentPath);
			
		              } else if (url != null) {
			
			              List<String> parts = Arrays.explode ("?", url);
			              currentPath = storage.getItem (currentPath.getShortFile (), Files.getName (parts.get (0), true));
			
			              new CreateFile ("file").execute (dialog);
			
		              } else //if (allowNames.length > 0 && !Arrays.contains (fileChosen, allowNames) || allowNames.length < 0)
			              newFileDialogShow (R.string.title_save, R.string.file_hint, currentPath, false, dialog);
		              
	              } catch (StorageException e) {
		              errors.add (e);
	              }
	              
              }
              
            });
            
            if (selectFileListener == null)
              dialog.getButton (AlertDialog.BUTTON_NEUTRAL).setOnClickListener (new View.OnClickListener () { // Создать папку
                
                @Override
                public void onClick (View view) {
                	
	                try {
		                newFileDialogShow (R.string.button_new_dir, R.string.dir_hint, currentPath, true, dialog);
	                } catch (StorageException e) {
		                errors.put (e);
	                }
	                
                }
                
              });
            
            dialog.getButton (AlertDialog.BUTTON_NEGATIVE).setOnClickListener (new View.OnClickListener () {
              
              @Override
              public void onClick (View view) {
                dialog.dismiss ();
              }
              
            });
            
          }
          
          //if (mDialog != null) mDialog.dismiss ();
          
        } else {
          
          for (Exception e : errors)
            error (e);
          
        }
        
        progress.dismiss ();
        
      }
      
    }
    
    /*public FileDialog setIcons (Map<String, Integer> icons) {
      return this;
    }*/
    
    public void show () {
      new ShowItems ().execute ();
    }
    
    private void error (Exception e) {
      
      if (dirListener != null)
        dirListener.onError (e);
      else if (fileReadListener != null)
        fileReadListener.onError (e);
      else if (fileListener != null)
        fileListener.onError (e);
      else if (selectFileListener != null)
        selectFileListener.onError (e);
      
    }
    
    private int layout = -1;
    
    public FileDialog setLayout (int layout) {
      
      this.layout = layout;
      return this;
      
    }
    
    private int style = -1;
    
    public FileDialog setStyle (int style) {
      
      this.style = style;
      return this;
      
    }
    
    private void fileExists (Item file, final DialogInterface dialog) {
      
      AlertDialog.Builder newBuilder = dialogBuilder ();
      
      newBuilder.setTitle (R.string.title_alert);
      newBuilder.setMessage (activity.getString (R.string.message_file_exists).replace ("%f", file.toString ()));
      
      newBuilder.setPositiveButton (R.string.yes, new DialogInterface.OnClickListener () {
        
        @Override
        public void onClick (DialogInterface dialog, int id) {}
        
      });
      
      newBuilder.setNegativeButton (R.string.no, new DialogInterface.OnClickListener () {
        
        @Override
        public void onClick (DialogInterface dialog, int id) {}
        
      });
      
      final AlertDialog newDialog = newBuilder.create ();
      newDialog.show ();
      
      newDialog.getButton (AlertDialog.BUTTON_POSITIVE).setOnClickListener (new View.OnClickListener () {
        
        @Override
        public void onClick (View view) {
          
          new CreateFile ("file").execute ();
          
          if (dialog != null) dialog.dismiss ();
          newDialog.dismiss ();
          
        }
        
      });
      
      newDialog.getButton (AlertDialog.BUTTON_NEGATIVE).setOnClickListener (new View.OnClickListener () {
        
        @Override
        public void onClick (View view) {
          newDialog.dismiss ();
        }
        
      });
      
    }
    
    private String content = "";
    private List<String> items = new ArrayList<> ();
    
    public FileDialog setFileContent (String content) {
      
      this.content = content;
      return this;
      
    }
    
    public FileDialog setFileContent (List<String> items) {
      
      this.items = items;
      return this;
      
    }
    
    private String[] fileEndsWith = {};
    
    public FileDialog setFileType (String... type) {
      
      fileEndsWith = type;
      return this;
      
    }
    
    private String[] allowNames = {};
    
    public FileDialog setFileNameShow (String... type) {
      
      allowNames = type;
      return this;
      
    }
    
    private String saveName = "";
    
    private void newFileDialogShow (int title, int hint, final Item fileChosen, final boolean createDir, final AlertDialog oldDialog) throws StorageException {
      
      if (saveName.equals ("")) {
        
        AlertDialog.Builder builder = dialogBuilder ();
        builder.setTitle (title);
        
        final View view = LayoutInflater.from (activity).inflate (R.layout.dialog_input, null);
        
        builder.setView (view);
        
        EditText input = (EditText) view.findViewById (R.id.text1);
        input.setHint (hint);
        
        builder.setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener () {
          
          @Override
          public void onClick (DialogInterface dialog, int id) {
            // Должен быть пустым, так как мы подвешиваем свой обработчик далее
          }
          
        });
        
        builder.setNegativeButton (android.R.string.cancel, new DialogInterface.OnClickListener () {
          
          @Override
          public void onClick (DialogInterface dialog, int id) {}
          
        });
        
        final AlertDialog dialog = builder.create ();
        dialog.show ();
        
        dialog.getButton (AlertDialog.BUTTON_POSITIVE).setOnClickListener (new View.OnClickListener () {
          
          @Override
          public void onClick (View v) { // Нажали OK после ввода имени папки или файла
            
            EditText input = (EditText) view.findViewById (R.id.text1);
            String mFile = input.getText ().toString ();
            
            if (createDir) { // Создаем папку
              
              if (!mFile.equals ("")) {
                
                currentPath = storage.setDir (fileChosen.getShortFile (), mFile);
                new CreateFile ("folder").execute (dialog, oldDialog);
                
              } else error (new IOException (activity.getString (R.string.message_dir_empty)));
              
            } else { // Пишем в файл
              
              if (!mFile.equals ("")) {
               
              	try {
		
		              if (Int.size (fileEndsWith) > 0) mFile += "." + fileEndsWith[0]; // TODO
		              currentPath = storage.getItem (fileChosen.getShortFile (), mFile);
		
		              new CreateFile ("file").execute (dialog, oldDialog);
		              
	              } catch (StorageException e) {
		              error (e);
	              }
              	
              } else error (new IOException (activity.getString (R.string.message_filename_empty)));
              
            }
            
          }
          
        });
        
        dialog.getButton (AlertDialog.BUTTON_NEGATIVE).setOnClickListener (new View.OnClickListener () {
          
          @Override
          public void onClick (View v) {
            dialog.dismiss ();
          }
          
        });
        
      } else {
        
        currentPath = storage.getItem (fileChosen.getShortFile (), saveName);
        new CreateFile ("file").execute (oldDialog);
        
      }
      
    }
    
    private class CreateFile extends AsyncTask<AlertDialog, Void, Integer> {
      
      private String type;
      private AlertDialog[] params;
      private List<Exception> errors = new ArrayList<> ();
      
      private ProgressDialog progress;
      
      private CreateFile (String type) {
        this.type = type;
      }
      
      @Override
      protected void onPreExecute () {
        
        progress = ProgressDialog.show (activity, null, activity.getString (R.string.loading));
        progress.setCancelable (false);
        
        super.onPreExecute ();
        
      }
      
      @Override
      public Integer doInBackground (AlertDialog... params) {
        
        this.params = params;
        
        try {
          
          if (type.equals ("file")) {
            
            if (url != null)
              storage.copy (new URL (url), currentPath);
            else if (Int.size (items) > 0)
              storage.put (items, currentPath);
            else
              storage.put (content, currentPath);
            
          } else return storage.makeDir (currentPath);
          
        } catch (StorageException | IOException | OutOfMemoryException e) {
          errors.add (e);
        }
        
        return 0;
        
      }
      
      @Override
      public void onPostExecute (Integer result) {
        
        progress.dismiss ();
        
        if (Int.size (errors) > 0) {
          
          for (Exception e : errors)
            error (e);
          
        } else {
          
          if (type.equals ("folder")) {
            
            switch (result) {
              
              case 0:
                error (new IOException ("Can't create dir " + currentPath));
                break;
              
              case 1: {
                
                if (Int.size (params) > 0) params[0].dismiss ();
                if (Int.size (params) > 1) params[1].dismiss ();
	
	              // И заходим в нее после создания
	
	              level = 1;
                new ShowItems ().execute ();
                
                if (dirListener != null)
                  dirListener.onCreateDir (currentPath);
                else if (fileReadListener != null)
                  fileReadListener.onCreateDir (currentPath);
                else if (fileListener != null)
                  fileListener.onCreateDir (currentPath);
                
                break;
                
              }
              
              case 2:
                error (new IOException (activity.getString (R.string.message_dir_exists)));
                break;
              
            }
            
          } else {
            
            if (Int.size (params) > 0) params[0].dismiss ();
            if (Int.size (params) > 1) params[1].dismiss ();
            
            if (fileReadListener != null)
              fileReadListener.onSave (currentPath, content);
            else if (fileListener != null)
              fileListener.onSave (currentPath, content);
            
            currentPath = null;
            
          }
          
        }
        
      }
      
    }
    
    private AlertDialog.Builder dialogBuilder () {
     
    	AlertDialog.Builder builder;
    	
    	if (OS.SDK >= 11)
        builder = new AlertDialog.Builder (activity, style);
      else
      	builder = new AlertDialog.Builder (activity);
      
      return builder;
      
    }
    
  }