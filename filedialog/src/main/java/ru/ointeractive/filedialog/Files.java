	package ru.ointeractive.filedialog;
	/*
	 Created by Acuna on 11.05.2018
	*/
	
	import android.graphics.Bitmap;
 
	import ru.ointeractive.andromeda.graphic.Graphic;
	import ru.ointeractive.jstorage.Item;
	import ru.ointeractive.jstorage.StorageException;
	import ru.ointeractive.storage.Storage;
	import upl.core.Log;
	import upl.core.exceptions.OutOfMemoryException;
	import upl.util.HashMap;
	import upl.util.List;
	import upl.util.Map;
	
	public class Files extends Provider {
	 
		private Map<String, Integer> icons = new HashMap<> ();
		
		Files (Item item) {
			
			setItem (item);
			
			icons.put ("text/directory", R.drawable.ic_folder_gray_24dp);
			icons.put ("application/octet-stream", R.drawable.ic_file_gray_24dp);
			
		}
		
		public Files setIcons (Map<String, Integer> icons) {
			
			this.icons = icons;
			return this;
			
		}
		
		@Override
		public Provider newInstance (Item item) {
			return new Files (item);
		}
		
		@Override
		public List<Provider> list () throws StorageException, OutOfMemoryException {
			
			List<Provider> files = super.list ();
			return sort (files);
			
		}
		
		@Override
		public Bitmap getImage () throws StorageException {
			
			int icon;
			
			if (item.getShortFile ().equals (FileDialog.PARENT_DIR) || item.isDir)
				icon = icons.get ("text/directory");
			else
				icon = icons.get ("application/octet-stream");
			
			return Graphic.toBitmap (((Storage) item.storage).context, icon);
			
		}
		
		@Override
		public String folderTitle () {
			return ru.ointeractive.jabadaba.Files.getName (item.getShortFile (), true);
		}
		
	}