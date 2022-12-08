package com.sf.tool.modifyentityclass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;

public class ListQueryApplication {

	private static final String PATH = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\project\\trunk\\eloan-parent\\eloan-dao\\src\\main\\java\\com\\scsb\\eloan\\";

	public static final void main(String... args) {

		Stream.concat(Arrays.stream(Paths.get(PATH, "edw", "dao").toFile().listFiles()), 
				Arrays.stream(Paths.get(PATH, "dwt", "dao").toFile().listFiles()))
		.forEach(f -> {
			try {
				System.out.println("=======" + f.getName() + "========");
				parseDaoGetQuery(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static void parseDaoGetQuery(File file) throws IOException {
		ASTParser parser = ASTParser.newParser(AST.JLS8);

		parser.setSource(FileUtils.readFileToString(file, "UTF-8").toCharArray());

		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			
			@Override
			public boolean visit(NormalAnnotation node) {
				
				if ("Query".equals(node.getTypeName().toString())) {
//					System.out.println(node.getTypeName());
					
					MemberValuePair mp = (MemberValuePair) node.values().get(0);
//					InfixExpression exp = (InfixExpression) mp.getValue();
//					System.out.println(mp.getValue() + ";");
					
					ScriptEngineManager mgr = new ScriptEngineManager();
					ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
					try {
						jsEngine.eval("print(" + mp.getValue().toString() + ");");
					} catch (ScriptException ex) {
						ex.printStackTrace();
					}
					
				}
				return false;
			}
		});
	}

}
