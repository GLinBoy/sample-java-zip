package com.glinboy.sample.zipper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class App {
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
			if (cmd.hasOption("help")) {
				helper.printHelp("Usage:", options);
			}
			if (cmd.hasOption("resource")) {
				log.info("has resource variable: {}", cmd.getParsedOptionValue("resource"));
			}
			if (cmd.hasOption("password")) {
				log.info("has password variable: {}", cmd.getParsedOptionValue("password"));
			}
			if (cmd.hasOption("size")) {
				log.info("has size variable: {}", cmd.getParsedOptionValue("size"));
			}
			if (cmd.hasOption("output")) {
				log.info("has output variable: {}", cmd.getParsedOptionValue("output"));
			}
		} catch (ParseException e) {
			log.error(e.getMessage());
			helper.printHelp("Usage:", options);
			System.exit(0);
		}
	}
}
