	package pro.acuna.filedialog;
	/*
	 Created by Acuna on 11.05.2018
	*/
	
	import android.graphics.drawable.Drawable;
	
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	
	import pro.acuna.andromeda.Graphic;
	import pro.acuna.filedialog.R;
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	import pro.acuna.storage.Item;
	import pro.acuna.storage.StorageException;
	
	public class Files extends Provider {
	 
		private Map<String, Integer> icons = new HashMap<> ();
		
		public Files (Item item) {
			
			super (item);
			
			icons.put ("text/directory", R.drawable.ic_folder_gray_24dp);
			icons.put ("application/octet-stream", R.drawable.ic_file_gray_24dp);
			
		}
		
		public Files setIcons (Map<String, Integer> icons) {
			
			this.icons = icons;
			return this;
			
		}
		
		@Override
		public Drawable folderCover () throws StorageException {
			return null;
		}
		
		@Override
		public Provider toProvider (Item item) {
			return new Files (item);
		}
		
		@Override
		public boolean isDir () {
			return item.isDir ();
		}
		
		@Override
		public List<Provider> list () throws StorageException, OutOfMemoryException {
			
			List<Provider> files = super.list ();
			return sort (files);
			
		}
		
		@Override
		public Drawable getImage () throws StorageException, OutOfMemoryException {
			
			int icon;
			
			if (item.getFile ().equals (FileDialog.PARENT_DIR) || isDir ())
				icon = icons.get ("text/directory");
			else
				icon = icons.get ("application/octet-stream");
			
			return Graphic.toDrawable (item.storage.context, icon);
			
		}
		
		@Override
		public String folderTitle () {
			return pro.acuna.jabadaba.Files.getName (item.getFile (), true);
		}
		
	}