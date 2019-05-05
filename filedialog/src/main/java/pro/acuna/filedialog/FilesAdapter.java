	package pro.acuna.filedialog;
	/*
	 Created by Acuna on 19.12.2018
	*/
	
	import android.app.Activity;
	import android.support.annotation.NonNull;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
	import android.widget.ArrayAdapter;
	import android.widget.ImageView;
	import android.widget.TextView;
	
	import java.util.List;
	
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	import pro.acuna.storage.StorageException;
	
	public class FilesAdapter extends ArrayAdapter<Provider> {
		
		private int layout;
		private List<Provider> filesList;
		
		private Listener listener;
		
		interface Listener {
			
			void onView (ViewHolder holder);
			
		}
		
		public FilesAdapter setListener (Listener listener) { // TODO: Unite with global listener?
			
			this.listener = listener;
			return this;
			
		}
		
		public FilesAdapter (Activity activity, int layout, int listLayout, List<Provider> filesList) {
			
			super (activity, layout, filesList);
			
			this.layout = listLayout;
			this.filesList = filesList;
			
		}
		
		@NonNull
		public View getView (int position, View view, @NonNull ViewGroup parent) {
			
			ViewHolder holder;
			
			if (view == null) {
				
				holder = new ViewHolder ();
				
				LayoutInflater inflater = LayoutInflater.from (parent.getContext ());
				view = inflater.inflate (layout, parent, false);
				
				holder.imageView = view.findViewById (R.id.icon);
				holder.textView = view.findViewById (R.id.title);
				
				view.setTag (holder);
				
			} else holder = (ViewHolder) view.getTag ();
			
			Provider item = filesList.get (position);
			
			try {
				
				holder.imageView.setImageDrawable (item.getImage ());
				holder.textView.setText (item.folderTitle ()); // TODO: Gray color if hidden
				
				if (listener != null) listener.onView (holder);
				
			} catch (StorageException | OutOfMemoryException e) {
				holder.textView.setText (e.getMessage ());
			}
			
			return view;
			
		}
		
		static class ViewHolder {
			
			ImageView imageView;
			TextView textView;
			
		}
		
	}