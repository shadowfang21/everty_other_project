package com.sf.tool.modifyentityclass;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;

import com.sf.tool.modifyentityclass.config.TableColumnDefConfiguration;

public class ModifyEntityApplication {

	private static final String EDW_PATH = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\project\\trunk\\eloan-parent\\eloan-dao\\src\\main\\java\\com\\scsb\\eloan\\edw\\";
	private static final String EDW_ENTITY_PATH = EDW_PATH + "\\entity";
	

	public static final void main(String... args) throws IOException {

		TableColumnDefConfiguration config = new TableColumnDefConfiguration();
		
		config.load();
		
//		Arrays.stream(new File(EDW_ENTITY_PATH).listFiles())
//		.filter(f -> "PartyEntity.java".equals(f.getName()))
//		.limit(1)
//		.forEach(f -> {
////			System.out.println(f.getName());
//			try {
//				parseEntity(f);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
	}
	
	//@Convert(converter = EdwTrimConverter.class)
	//import javax.persistence.Convert;
	//import com.scsb.eloan.entity.converter.EdwTrimConverter;

	public static void parseEntity(File file) throws IOException, MalformedTreeException, BadLocationException, JavaModelException, IllegalArgumentException {
		ASTParser parser = ASTParser.newParser(AST.JLS8);

		String source = FileUtils.readFileToString(file, "UTF-8");
		
		parser.setSource(source.toCharArray());

		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		//add import
//		cu.recordModifications();
//		cu.imports().add(createImportConverter(cu.getAST()));
//		cu.imports().add(createImportEdwConverter(cu.getAST()));
		
		cu.accept(new ASTVisitor() {
			
			@Override
			public boolean visit(FieldDeclaration node) {
				
				if ("String".equals(node.getType().toString())) {
					Optional<Pair<String, Integer>> opt = getColumnNameAndIndex(node);
					
					if (opt.isPresent()) {
						if ("PARTY_ID".equals(opt.get().getLeft())) {
							System.out.println(node.modifiers());
							
							for (Object o : node.modifiers()) {
								if (o instanceof NormalAnnotation) {
									NormalAnnotation n = (NormalAnnotation) o;
									
									System.out.println(n.getTypeNameProperty());
								}
							}
							
						}
//						System.out.println(getColumnName(opt.get()));
//						node.modifiers().add(opt.get().getRight(), createConverter(node.getAST()));
					}
				}
				return false;
			}

		});
		
//		cu.accept(new ASTVisitor() {
//			
//			@Override
//			public boolean visit(NormalAnnotation node) {
//				
//				if ("Converter".equals(node.getTypeName().toString())) {
//					System.out.println(node);
//				}
//				return false;
//			}
//		});
		
//		Document document = new org.eclipse.jface.text.Document(source);
//		
//		TextEdit edits = cu.rewrite(document, null);
//		edits.apply(document);
//		
//		FileUtils.write(file, document.get(), "UTF-8");
	}
	
	public static Optional<Pair<String, Integer>> getColumnNameAndIndex(FieldDeclaration node) {
		AtomicInteger idx = new AtomicInteger(0);
		return node.modifiers().stream()
			.map(s -> {
				idx.incrementAndGet();
				return s;
			})
			.filter(m -> m instanceof NormalAnnotation)
			.filter(c -> "Column".equals(((NormalAnnotation)c).getTypeName().toString()))
			.map(c -> Pair.of(getColumnName((NormalAnnotation)c), idx.get()))
			.findFirst();
	}
	
	public static String getColumnName(NormalAnnotation node) {
		MemberValuePair mp = (MemberValuePair) node.values().get(0);
		return ((StringLiteral) mp.getValue()).getLiteralValue();
	}
	public static ImportDeclaration createImportConverter(AST ast) {
		ImportDeclaration im = ast.newImportDeclaration();
		im.setStatic(false);
		im.setName(ast.newName("javax.persistence.Convert"));
		return im;
	}
	public static ImportDeclaration createImportEdwConverter(AST ast) {
		ImportDeclaration im = ast.newImportDeclaration();
		im.setStatic(false);
		im.setName(ast.newName("com.scsb.eloan.entity.converter.EdwTrimConverter"));
		return im;
	}
	
	public static NormalAnnotation createConverter(AST ast) {
		NormalAnnotation converter = ast.newNormalAnnotation();
		
		converter.setTypeName(ast.newName("Convert"));
		MemberValuePair mp = ast.newMemberValuePair();
		mp.setName(ast.newSimpleName("converter"));
		
		TypeLiteral tl = ast.newTypeLiteral();
		tl.setType(ast.newSimpleType(ast.newName("EdwTrimConverter")));
		mp.setValue(tl);
		converter.values().add(mp);
		
		return converter;
	}
}
