    private static String createInsertSql(String template, String[] columns, String[] values) {
        for (int i = 0; i < values.length; i++) {
            String value = values[i].trim();
            if ("\"\"".equals(value)) {
                template = template.replaceFirst("\\?", "NULL");
            } else {
                value = value.replaceAll("^\"|\"$", ""); // Remove surrounding double quotes if any
                if (isDateColumn(columns[i])) {
                    value = "TO_DATE('" + value + "', 'DD-MON-YY')";
                } else {
                    value = "'" + value + "'";
                }
                template = template.replaceFirst("\\?", value);
            }
        }
        return template;
    }
