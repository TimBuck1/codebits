private static String createInsertSql(String template, String[] values) {
        for (String value : values) {
            if ("\"\"".equals(value)) {
                template = template.replaceFirst("\\?", "NULL");
            } else {
                value = value.replaceAll("^\"|\"$", ""); // Remove surrounding double quotes if any
                template = template.replaceFirst("\\?", "'" + value + "'");
            }
        }
        return template;
    }
