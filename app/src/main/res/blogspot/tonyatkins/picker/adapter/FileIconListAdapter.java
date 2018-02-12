/**
 * Copyright 2013 Tony Atkins <duhrer@gmail.com>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Tony Atkins ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Tony Atkins OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package com.blogspot.tonyatkins.picker.adapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.blogspot.tonyatkins.picker.Constants;
import com.blogspot.tonyatkins.picker.LabeledFile;
import com.blogspot.tonyatkins.picker.R;
import com.blogspot.tonyatkins.picker.activity.FilePickerActivity;
import com.blogspot.tonyatkins.picker.listener.DirectoryPickedListener;
import com.blogspot.tonyatkins.picker.listener.FilePickedListener;

public class FileIconListAdapter implements ListAdapter {
	public static final int SOUND_FILE_TYPE = 1;
	public static final int IMAGE_FILE_TYPE = 2;
	public static final int BACKUP_FILE_TYPE = 3;

	public static final String DEFAULT_DIR = Constants.HOME_DIR;

	private FilePickerActivity activity;
	private String cwd = DEFAULT_DIR;
	private List<File> files = null;
	private int fileType;
	private final FileFilter filter;

	public FileIconListAdapter(FilePickerActivity activity, int fileType) {
		super();
		this.activity = activity;
		this.fileType = fileType;
		filter = new SimpleFileFilter();

		// set the list of files based on our current directory
		loadFiles();
	}

	private void loadFiles() {
		File workingDirectory = new File(cwd);
		if (workingDirectory.isDirectory())
		{
			File[] workingDirFiles = workingDirectory.listFiles(filter);
			files = new ArrayList<File>();
			// If this isn't the root directory, add the parent to the list
			// FIRST
			String parentDir = getParentDir();
			if (parentDir != null)
			{
				// Distinguish the parent directory from the children
				files.add(new LabeledFile(parentDir, "Parent Directory"));
			}
			// add all the rest of the files to the list of files
			for (File file : workingDirFiles)
			{
				files.add(file);
			}
		}
	}

	private String getParentDir() {
		if (displayParentDirectory())
		{
			return cwd.replaceFirst("/[^/]+/?$", "");
		}

		return null;
	}

	private boolean displayParentDirectory() {
		String deviceRootPath = Environment.getExternalStorageDirectory().getPath();
		return (cwd.startsWith(deviceRootPath) && !cwd.matches("^" + deviceRootPath + "/?$"));
	}

	public int getCount() {
		if (files != null)
		{
			return files.size();
		}

		return 0;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (position > files.size())
			return null;

		File file = files.get(position);

		// return an icon with the right name and image type
		int iconResource = R.drawable.file;
		if (file.isDirectory()) {
            if (file instanceof LabeledFile && ((LabeledFile) file).getLabel().equals("Parent Directory")) {
                iconResource = R.drawable.up;
            }
            else {
                iconResource = R.drawable.folder;
            }
        }

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.file_picker_row, parent, false);

		ImageView iconView = (ImageView) view.findViewById(R.id.filePickerRowIcon);
		iconView.setImageResource(iconResource);
		
		TextView labelView = (TextView) view.findViewById(R.id.filePickerRowLabel);
		if (file instanceof LabeledFile) {
			labelView.setText(getCleanLabel(((LabeledFile)file).getLabel()));
		}
		else {
			labelView.setText(getCleanLabel(file.getName()));
		}
		
		if (file.isDirectory())
		{
			view.setOnClickListener(new DirectoryPickedListener(activity, file.getAbsolutePath()));
		}
		else
		{
			// TODO: If we're working with images, display a thumbnail
			view.setOnClickListener(new FilePickedListener(activity, file));
		}

		return view;
	}

	public int getItemViewType(int position) {
		return 0;
	}

	public int getViewTypeCount() {
		return 1;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isEmpty() {
		return false;
	}

	public void registerDataSetObserver(DataSetObserver observer) {
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	public boolean areAllItemsEnabled() {
		return true;
	}

	public boolean isEnabled(int position) {
		return true;
	}

	private class SimpleFileFilter implements FileFilter {
		public boolean accept(File file) {
			// exclude null files
			if (file == null)
				return false;

			// include all directories
			if (file.isDirectory())
			{
				if (file.getName().startsWith("."))
					return false;
				else
					return true;
			}

			// if it's a file and empty, exclude it
			if (file.length() <= 0)
				return false;

			// Do not display non-sound files
			else if (fileType == SOUND_FILE_TYPE && file.getName().matches("^.+.(wav|mp3|ogg|3gp)$"))
			{
				return true;
			}
			// Do not display non-image files
			else if (fileType == IMAGE_FILE_TYPE && file.getName().matches("^.+.(png|gif|jpg|bmp|jpeg)$"))
			{
				return true;
			}
			// Do not display non-backup (ZIP) files
			else if (fileType == BACKUP_FILE_TYPE && file.getName().matches("^.+.zip$"))
			{
				return true;
			}

			return false;
		}
	}

	public String getCwd() {
		return cwd;
	}

	public void setCwd(String cwd) {
		this.cwd = cwd;
		loadFiles();
	}

	private String getCleanLabel(String label) {
		String cleanLabel = label;
		if (label.length() > 20)
		{
			cleanLabel = label.substring(0, 17) + "...";
		}
		return cleanLabel;
	}
}
