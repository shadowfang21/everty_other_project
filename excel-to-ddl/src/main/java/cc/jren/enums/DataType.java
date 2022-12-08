package cc.jren.enums;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

import cc.jren.model.Column;

public enum DataType {
	
	CLOB(typeStartsWith("JSON")),
	NUMBER(typeStartsWith("數字"), numberLength()),
	TIMESTAMP(typeStartsWith("日期")
			.and(typeDescStartsWith("YYYY/MM/DD HH:MM")
				.or(typeDescStartsWith("YYYYMMDD HH:MM"))
				.or(typeDescStartsWith("YYYYMMDD HHMM")))),
	DATE(typeStartsWith("日期")
			.and(typeDescStartsWith("YYYY/MM/DD")
				.or(typeDescStartsWith("YYYYMMDD")
				.or(typeDescStartsWith("YYYY/MM"))))),
	VARCHAR2(c -> true, varchar2Length().andThen(s -> {
	    return Pair.of(s.getLeft() > 4000 ? 4000 : s.getLeft(), 0);
	}));
	
	private Predicate<Column> checker;
	private Function<Column, Pair<Integer, Integer>> lengthFun;
	
	private DataType(Predicate<Column> checker) {
		this.checker = checker;
		this.lengthFun = (c) -> Pair.of(19,0);
	}
	
	private DataType(Predicate<Column> checker, Function<Column, Pair<Integer, Integer>> lengthFun) {
		this.checker = checker;
		this.lengthFun = lengthFun;
	}

	public Predicate<Column> getChecker() {
		return checker;
	}
	public Function<Column, Pair<Integer, Integer>> getLengthFun() {
		return lengthFun;
	}
	
	private static Pattern startWithNumberPattern = Pattern.compile("^(\\d+).*");
	private static Pattern numberFormatPattern = Pattern.compile("^[\\d,]+(\\.\\d+)?$");
	
	private static Pattern directNumberFormatPattern = Pattern.compile("^\\((\\d+)\\s*,\\s*(\\d+)\\)$");
	
	private static Function<Column, Pair<Integer, Integer>> varchar2Length() {
		return (c) -> {
			
			Matcher matcher = startWithNumberPattern.matcher(c.getTypeDesc());
			
			if (matcher.find()) {
				if (c.getTypeDesc().contains("字") && !c.getTypeDesc().contains("英")) {
					return Pair.of(Integer.valueOf(matcher.group(1)) * 3, 0);
				} else {
					return Pair.of(Integer.valueOf(matcher.group(1)), 0);
				}
			} else {
				if (c.getColumnName().endsWith("_YN")) {
					return Pair.of(1,0);
				} else if (c.getType().startsWith("RADIO") || c.getType().startsWith("下拉選單")) {
					return Pair.of(2,0);
				} else if (c.getType().startsWith("CHECK")) {
					return Pair.of(100,0);
				}
			}
			throw new RuntimeException("varchar2 length fail : " + c.toString());
		};
	}
	
	private static Function<Column, Pair<Integer, Integer>> numberLength() {
		return (c) -> {
			if (numberFormatPattern.matcher(c.getTypeDesc()).matches()) {
				return Optional.of(c.getTypeDesc().split("\\."))
					.map(s -> {
						if (s.length == 1) {
							return Pair.of(s[0].replace(",", "").length(), 0);
						} else if (s.length == 2){
							return Pair.of(s[0].replace(",", "").length() + s[1].replace(",", "").length(), s[1].replace(",", "").length());
						}
						return Pair.of(19,0);
					})
					.orElseThrow(() -> new RuntimeException(c.toString()));
			} else if (directNumberFormatPattern.matcher(c.getTypeDesc()).matches()) {
				
				Matcher matcher = directNumberFormatPattern.matcher(c.getTypeDesc());
				if (matcher.find()) {
					return Pair.of(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
				}
			} else {
			    Matcher matcher = startWithNumberPattern.matcher(c.getTypeDesc());
	            
	            if (matcher.find()) {
	                return Pair.of(Integer.valueOf(matcher.group(1)), 0);
	            }
			}
			
			return Pair.of(19,0);
		};
	}
	
	private static Predicate<Column> typeStartsWith(String text) {
		return c -> c.getType().startsWith(text);
	}
	private static Predicate<Column> typeDescStartsWith(String format) {
		return c -> c.getTypeDesc().startsWith(format);
	}
}
