package com.glinboy.sample.zipper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

@Log4j2
public class App {

	public static final String ZIP_FILE_NAME = "zipped.zip";

	public static void main(String[] args) {
		Options options = new Options();
		Option fileOption = Option.builder("r")
				.longOpt("resource")
				.argName("resource")
				.desc("File/Directory path to compress")
				.type(String.class)
				.required(true)
				.hasArg()
				.build();
		Option secretOption = Option.builder("p")
				.longOpt("password")
				.argName("password")
				.desc("A secret to secure compressed file")
				.type(String.class)
				.hasArg()
				.build();
		Option sizeOption = Option.builder("s")
				.longOpt("size")
				.argName("size")
				.desc("Size of output files (on MB)")
				.type(Number.class)
				.hasArg()
				.build();
		Option outputOption = Option.builder("o")
				.longOpt("output")
				.argName("output")
				.desc("Path to save result of compression")
				.type(String.class)
				.hasArg()
				.build();
		Option helpOption = Option.builder("h")
				.longOpt("help")
				.argName("help")
				.desc("print this message")
				.hasArg(false)
				.build();
		
		options.addOption(fileOption);
		options.addOption(secretOption);
		options.addOption(sizeOption);
		options.addOption(helpOption);
		options.addOption(outputOption);
		CommandLine cmd;
		CommandLineParser parser = new DefaultParser();
		HelpFormatter helper = new HelpFormatter();
		try {
			cmd = parser.parse(options, args);

			String path = cmd.getOptionValue("resource");
			Boolean isDirectory = Files.isDirectory(Paths.get(path));
			char[] password = null;
			Long size = 0L;
			String output = null;

			if (Files.notExists(Paths.get(path))) {
				log.error("Resource dosen't exist.");
				System.exit(0);
			}
			if (cmd.hasOption("help")) {
				helper.printHelp("Usage:", options);
				System.exit(0);
			}
			if (cmd.hasOption("password")) {
				password = cmd.getOptionValue("password").toCharArray();
			}
			if (cmd.hasOption("size")) {
				size = Long.parseLong(cmd.getOptionValue("size"));
			}
			if (cmd.hasOption("output")) {
				output = cmd.getOptionValue("output");
			} else {
				output = ZIP_FILE_NAME;
			}
			List<File> filesToAdd = Arrays.asList(new File(path));
			ZipFile zipfile = password == null ? new ZipFile(output) : new ZipFile(output, password);
			ZipParameters zipParameters = new ZipParameters();
			zipParameters.setEncryptFiles(true);
			zipParameters.setEncryptionMethod(EncryptionMethod.AES);
			zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
			if (isDirectory) {
				zipfile.addFolder(new File(path), zipParameters);
			} else {
				if (size > 0L) {
					zipfile.createSplitZipFile(filesToAdd, zipParameters, size > 0, size * 1024L);
				} else {
					zipfile.addFiles(filesToAdd, zipParameters);
				}
			}
			zipfile.close();
		} catch (ParseException e) {
			log.error("Parsing command line input has been failed: {}=", e.getMessage());
			helper.printHelp("Usage:", options);
			System.exit(0);
		} catch (ZipException ex) {
			log.error("Can't compress: {}", ex.getMessage(), ex);
		} catch (IOException ex) {
			log.error("Can't close zip file: {}", ex.getMessage(), ex);
		}
	}
}
