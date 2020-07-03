package org.ufpe.simulationtopologyfactory.application;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import br.ufpe.simulator.utils.ConvertUtils;

public class ConvertToProperties {

	public static void main(String[] args) {
		File file = new File("src/main/resources/NSFNet.txt"); // Change this to your file name
		int count = 0;
		try {
			Scanner fileReader = new Scanner(file);
			while (fileReader.hasNext()) {
				String fst = fileReader.next();
				String snd = fileReader.next();
				String value = fileReader.next();

				int fst_ = ConvertUtils.convertToInteger(fst) + 1;
				int snd_ = ConvertUtils.convertToInteger(snd) + 1;

				if (fst_ < snd_) {
					System.out.println("l" + count++ + "=" + fst_ + " " + snd_ + " " + value);
				}
			}
			fileReader.close();
		} catch (IOException e) {
			// Handle error...
		}
	}
}
