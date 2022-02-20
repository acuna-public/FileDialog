  package ru.ointeractive.filedialog;
  /*
   Created by Acuna on 10.05.2018
  */
  
  import android.graphics.Bitmap;
  
  import java.io.IOException;
  import java.util.Collections;
  import java.util.Comparator;
  
  import ru.ointeractive.jabadaba.Int;
  import upl.core.Log;
  import upl.core.exceptions.HttpRequestException;
  import ru.ointeractive.jstorage.Item;
  import ru.ointeractive.jstorage.StorageException;
  import upl.core.exceptions.OutOfMemoryException;
  import java.util.ArrayList;
  import java.util.List;
	
  public abstract class Provider {
    
    public Item item;
    protected List<String> files = new ArrayList<> ();
    
    protected Provider () {}
    
    public Provider setItem (Item item) {
	    
      this.item = item;
      return this;
      
    }
    
    public Provider setFiles (List<String> files) {
      
      this.files = files;
      return this;
      
    }
    
    public abstract Provider newInstance (Item item);
    public abstract Bitmap getImage () throws IOException, HttpRequestException, StorageException, OutOfMemoryException;
    public abstract String folderTitle ();
    
    public upl.util.List<Provider> list () throws StorageException, OutOfMemoryException {
			
	    upl.util.List<Provider> output = new upl.util.ArrayList<> ();
	    
      if (Int.size (files) > 0) {
        
        for (String file : files)
          output.add (newInstance (item.storage.getItem (file).isDir (false)));
        
      } else {
        
        for (Item file : item.thumbsList ())
	        output.add (newInstance (file));
        
      }
      
      return output;
      
    }
    
    protected upl.util.List<Provider> sort (upl.util.List<Provider> list) {
      
      Collections.sort (list, new Comparator<Provider> () {
        
        @Override
        public int compare (Provider file1, Provider file2) {
          
          if (file1.item.isDir && !file2.item.isDir) return -1;
          else if (file2.item.isDir && !file1.item.isDir) return 1;
          else return file1.folderTitle ().compareTo (file2.folderTitle ());
          
        }
        
      });
      
      return list;
      
    }
    
    //@Override
    //@NonNull
    //public String toString () {
    //  return item.getShortFile ();
    //}
    
    public Object getItemId () {
      return item.getId ();
    }
    
  }