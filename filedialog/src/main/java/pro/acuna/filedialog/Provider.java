	package pro.acuna.filedialog;
	/*
	 Created by Acuna on 10.05.2018
	*/
	
	import android.graphics.drawable.Drawable;
	
	import org.json.JSONObject;
	
	import java.net.URL;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.Comparator;
	import java.util.List;
	
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	import pro.acuna.storage.Item;
	import pro.acuna.storage.StorageException;
	
	public abstract class Provider {
		
		public Item item;
		
		public Provider (Item item) {
			this.item = item;
		}
		
		public List<Provider> list () throws StorageException, OutOfMemoryException {
			
			List<Provider> output = new ArrayList<> ();
			
			List<Item> files = item.list ();
			
			for (Item file : files)
				output.add (toProvider (file));
			
			return output;
			
		}
		
		public String getUserId () {
			return item.storage.provider.getUserId ();
		}
		
		public String getNextId () {
			return item.storage.provider.getNextId ();
		}
		
		public JSONObject getOwnerData () {
			return item.storage.provider.getOutputData ();
		}
		
		protected List<Provider> sort (List<Provider> list) {
			
			Collections.sort (list, new Comparator<Provider> () {
				
				@Override
				public int compare (Provider file1, Provider file2) {
					
					if (file1.isDir () && !file2.isDir ()) return -1;
					else if (file2.isDir () && !file1.isDir ()) return 1;
					else return file1.folderTitle ().compareTo (file2.folderTitle ());
					
				}
				
			});
			
			return list;
			
		}
		
		public String getPath () {
			return item.getPath ();
		}
		
		public final String toString () {
			return getPath ();
		}
		
		public String getItemId () {
			return item.getId ();
		}
		
		public URL getDirectLink () throws StorageException {
			return item.getDirectLink ();
		}
		
		public int perPage () {
			return item.storage.provider.perPage ();
		}
		
		public Drawable folderCover () throws StorageException, OutOfMemoryException {
			return null;
		}
		
		public abstract Provider toProvider (Item item);
		public abstract boolean isDir ();
		public abstract Drawable getImage () throws StorageException, OutOfMemoryException;
		public abstract String folderTitle ();
		
	}