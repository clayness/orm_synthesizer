package edu.virginia.cs.Uniq;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.virginia.cs.Synthesizer.CodeNamePair;

public class DeleteUniq {
	public static void del(String path) {

		String dirPath = path;
		HashSet<String> uniqFileList = uniqueFiles(cksums(getFileList(dirPath)));
		
		File folder = new File(dirPath);

		ArrayList<String> delFiles = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			String filePath = fileEntry.getAbsolutePath();
			if (filePath.endsWith("xml") && !uniqFileList.contains(filePath)) {
				delFiles.add(filePath);
			}
		}
		
		// delete xml files
		for(String s : delFiles){
			new File(s).delete();
		}

	}

	// get file list by path
	public static ArrayList<String> getFileList(String dir) {
		ArrayList<String> lists = new ArrayList<String>();
		File folder = new File(dir);

		for (final File fileEntry : folder.listFiles()) {
			String filePath = fileEntry.getAbsolutePath();
			if (filePath.endsWith("xml")) {
				lists.add(filePath);
			}
		}
		return lists;
	}

	// get checksum of all files
	public static ArrayList<CodeNamePair<String>> cksums(ArrayList<String> files) {
		ArrayList<CodeNamePair<String>> cksums = new ArrayList<CodeNamePair<String>>();
		for (String s : files) {
			try {
				Process p = Runtime.getRuntime().exec("cksum " + s);
				p.waitFor();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String firstLine = reader.readLine();
				String[] splited = firstLine.split(" ");
				cksums.add(new CodeNamePair<String>(splited[2], splited[0]));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return cksums;
	}

	public static HashSet<String> uniqueFiles(
			ArrayList<CodeNamePair<String>> filesWithCksums) {
		ArrayList<String> sums = new ArrayList<String>();
		for (CodeNamePair<String> p : filesWithCksums) {
			sums.add(p.getSecond());
			//System.out.println(p.getSecond());
		}

		Set<String> uniqueSums = new HashSet<String>(sums);

		// find out unique files with HashSet
		HashSet<String> hashSet = new HashSet<String>();

		for (String u : uniqueSums) {
			for (CodeNamePair<String> p : filesWithCksums) {
				if (p.getSecond().equals(u)) {
					hashSet.add(p.getFirst());
					break;
				}
			}
		}

		return hashSet;
	}

	// compare files by contains
	public static ArrayList<String> compare(ArrayList<String> files) {
		int filesSize = files.size();
		// 0 for same, 1 for different
		int [][] matrix = new int[filesSize][filesSize];
		ArrayList<String> fileList = new ArrayList<String>();
		HashMap<String, Integer> fileMap = new HashMap<String, Integer>();
		Process p;
		try {
			for (int i = 0; i < files.size(); i++) {
				for (int j = 0; j < files.size(); j++) {
					String iFile = files.get(i);
					String jFile = files.get(j);
					p = Runtime.getRuntime()
							.exec("diff " + iFile + " " + jFile);
					p.waitFor();

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					String firstLine = reader.readLine();
					if (firstLine != null) {
						matrix[i][j] = 1;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// iterate map and copy to a list
		Set<String> set = fileMap.keySet();
		for (String s : set) {
			fileList.add(s);
		}
		return fileList;
	}
}
