{
<%
    table.columns.each { col ->
        if (utils.skipValidators(col) || utils.getValidators(col) == null) {
            out << "${col.javaColumnName} : null,\n";
        } else {
            out << "${col.javaColumnName} : [null, [${utils.getValidators(col)}]],\n";
        }
    }
%>
}