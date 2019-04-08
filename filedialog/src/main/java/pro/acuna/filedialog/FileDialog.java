	package pro.acuna.filedialog;
	/*
	 Created by Acuna on 15.09.2016
	*/
	
	import android.app.Activity;
	import android.app.AlertDialog;
	import android.app.ProgressDialog;
	import android.content.DialogInterface;
	import android.os.AsyncTask;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
	import android.widget.EditText;
	
	import org.json.JSONException;
	import org.json.JSONObject;
	
	import java.io.IOException;
	import java.net.URL;
	import java.util.ArrayList;
	import java.util.List;
	
	import pro.acuna.jabadaba.Arrays;
	import pro.acuna.jabadaba.Files;
	import pro.acuna.jabadaba.Int;
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	import pro.acuna.storage.Item;
	import pro.acuna.storage.Storage;
	import pro.acuna.storage.StorageException;
	import pro.acuna.storage.providers.SDCard;
	
	public class FileDialog {
		
		private Storage storage;
		public static final String PARENT_DIR = "..";
		private int level = 0;
		private Item currentPath;
		private Provider provider;
		private Activity activity;
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
		
		private String rootPath = "/";
		
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
		
		public enum Type {FILES, FOLDERS, ALL}
		
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
			setShowType (Type.FILES);
			
			return this;
			
		}
		
		public FileDialog setListener (FolderListener listener) {
			
			dirListener = listener;
			return this;
			
		}
		
		public FileDialog setListener (SelectFileListener listener) {
			
			selectFileListener = listener;
			setShowType (Type.FILES);
			
			return this;
			
		}
		
		private class ShowItems extends AsyncTask<Void, Void, List<Provider>> {
			
			private ProgressDialog progress;
			private List<Exception> errors = new ArrayList<> ();
			
			@Override
			protected void onPreExecute () {
				
				progress = ProgressDialog.show (activity, null, activity.getString (R.string.loading));
				progress.setCancelable (false);
				
			}
			
			@Override
			public List<Provider> doInBackground (Void... params) {
				
				List<Provider> files = new ArrayList<> ();
				
				try {
					
					if (storage == null) {
						
						storage = new Storage (activity);
						
						SDCard sdcard = new SDCard ();
						
						JSONObject data = new JSONObject ();
						
						data.put ("folder", rootPath);
						
						JSONObject data2 = new JSONObject ();
						data2.put (sdcard.getName (), data);
						
						storage.init (sdcard.getName (), data2);
						
					}
					
					if (currentPath == null) {
						
						if (rootPath != null) storage.makeDir (rootPath);
						currentPath = storage.toItem (rootPath).isDir (true);
						
					}
					
					provider = new pro.acuna.filedialog.Files (currentPath);
					
					provider = provider.toProvider (currentPath);
					List<Provider> mFiles = provider.list ();
					
					//if (currentPath.getParent () != null) {
					
					if (!currentPath.getParent ().getFile ().equals ("/"))
						files.add (provider.toProvider (storage.toItem (PARENT_DIR).isDir (true)));
					
					//}
					
					if (Int.size (mFiles) > 0) {
						
						for (Provider file : mFiles) {
							
							if (
								file.isDir () ||
								(
									showType != Type.FOLDERS &&
									(
										(Int.size (fileEndsWith) > 0 && Arrays.contains (Files.getExtension (file.toString ()), fileEndsWith))
										||
										Int.size (fileEndsWith) <= 0
									) && (
										(Int.size (allowNames) > 0 && Arrays.contains (Files.getName (file.toString ()), allowNames))
										||
										Int.size (allowNames) <= 0
									)
								)
							) {
								
								files.add (file);
								
								//if (allowNames.length > 0 && !file.isDir () && Arrays.contains (file.getName (), allowNames))
								//result = exists (fileChosen, dialog);
								
							}
							
						}
						
					}
					
				} catch (StorageException | JSONException | OutOfMemoryException e) {
					errors.add (e);
				}
				
				return files;
				
			}
			
			@Override
			public void onPostExecute (final List<Provider> files) {
				
				if (Int.size (errors) == 0) {
					
					AlertDialog.Builder builder = dialogBuilder (activity);
					
					if (title > 0 && level == 0)
						builder.setTitle (title);
					else
						builder.setTitle (currentPath.toString ());
					
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
						public void onClick (final DialogInterface dialog, final int id) { // Кликнули по файлу
							
							Item selected = files.get (id).item;
							
							if (Files.getName (selected.getFile (), true).equals (PARENT_DIR))
								currentPath = currentPath.getParent ();
							else
								currentPath = selected;
							
							if (currentPath.isDir ()) { // Открываем папку
								
								dialog.dismiss ();
								
								if (dirListener != null)
									dirListener.onSelect (currentPath);
								else if (fileReadListener != null)
									fileReadListener.onSelect (currentPath);
								else if (fileListener != null)
									fileListener.onSelect (currentPath, dialog);
								
								level = 1;
								new ShowItems ().execute ();
								
							} else { // Кликаем по существующему файлу и спрашиваем нужно ли его переписать
								
								if (fileReadListener != null) {
									
									content = fileReadListener.onOpen (currentPath);
									
									new CreateFile ("file").execute ();
									dialog.dismiss ();
									
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
							public void onClick (View v) {
								
								if (dirListener != null || selectFileListener != null) {
									
									dialog.dismiss ();
									
									if (dirListener != null)
										dirListener.onSubmit (currentPath);
									
								} else if (url != null) {
									
									List<String> parts = Arrays.explode ("?", url);
									currentPath = storage.toItem (currentPath.getFile (), Files.getName (parts.get (0), true));
									
									new CreateFile ("file").execute (dialog);
									
								} else //if (allowNames.length > 0 && !Arrays.contains (fileChosen, allowNames) || allowNames.length < 0)
									newFileDialogShow (R.string.title_save, R.string.file_hint, currentPath, false, dialog);
								
							}
							
						});
						
						if (selectFileListener == null)
							dialog.getButton (AlertDialog.BUTTON_NEUTRAL).setOnClickListener (new View.OnClickListener () { // Создать папку
								
								@Override
								public void onClick (View v) {
									
									newFileDialogShow (R.string.button_new_dir, R.string.dir_hint, currentPath, true, dialog);
								}
								
							});
						
						dialog.getButton (AlertDialog.BUTTON_NEGATIVE).setOnClickListener (new View.OnClickListener () {
							
							@Override
							public void onClick (View v) {
								
								dialog.dismiss ();
							}
							
						});
						
					}
					
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
			
			AlertDialog.Builder newBuilder = dialogBuilder (activity);
			
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
				public void onClick (View v) {
					
					new CreateFile ("file").execute ();
					
					if (dialog != null) dialog.dismiss ();
					newDialog.dismiss ();
					
				}
				
			});
			
			newDialog.getButton (AlertDialog.BUTTON_NEGATIVE).setOnClickListener (new View.OnClickListener () {
				
				@Override
				public void onClick (View v) {
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
		
		private void newFileDialogShow (int title, int hint, final Item fileChosen, final boolean createDir, final AlertDialog oldDialog) {
			
			if (saveName.equals ("")) {
				
				AlertDialog.Builder builder = dialogBuilder (activity);
				builder.setTitle (title);
				
				final ViewGroup parent = null;
				final View view = LayoutInflater.from (activity).inflate (R.layout.dialog_input, parent);
				
				builder.setView (view);
				
				EditText input = view.findViewById (R.id.text1);
				input.setHint (hint);
				
				builder.setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener () {
					
					@Override
					public void onClick (DialogInterface dialog, int id) {}
					// Должен быть пустым, так как мы подвешиваем свой обработчик далее
					
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
						
						EditText input = view.findViewById (R.id.text1);
						String mFile = input.getText ().toString ();
						
						if (createDir) { // Создаем папку
							
							if (!mFile.equals ("")) {
								
								currentPath = storage.toItem (fileChosen.getFile (), mFile).isDir (true);
								new CreateFile ("folder").execute (dialog, oldDialog);
								
							} else error (new IOException (activity.getString (R.string.message_dir_empty)));
							
						} else { // Пишем в файл
							
							if (!mFile.equals ("")) {
								
								if (Int.size (fileEndsWith) > 0) mFile += "." + fileEndsWith[0]; // TODO
								currentPath = storage.toItem (fileChosen.getFile (), mFile);
								
								new CreateFile ("file").execute (dialog, oldDialog);
								
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
				
				currentPath = storage.toItem (fileChosen.getFile (), saveName);
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
							storage.copy (new URL (url), currentPath.getFile ());
						else if (Int.size (items) > 0)
							storage.write (items, currentPath.getFile ());
						else
							storage.write (content, currentPath.getFile ());
						
					} else return storage.makeDir (currentPath.getFile ());
					
				} catch (StorageException | IOException e) {
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
								
								level = 1;
								new ShowItems ().execute ();
								
								// И переходим к ней после создания
								
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
		
		private AlertDialog.Builder dialogBuilder (Activity activity) {
			return new AlertDialog.Builder (activity, style);
		}
		
	}