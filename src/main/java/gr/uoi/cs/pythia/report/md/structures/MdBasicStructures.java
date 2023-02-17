package gr.uoi.cs.pythia.report.md.structures;

public class MdBasicStructures {

    public static String center(String text) {
//        // obsolete
//        return String.format("<center>\n\n%s\n\n</center>\n", text);
//        // does not center the table & obsolete
//        return String.format("<div style=\"text-align: center;\">\n\n%s\n\n</div>\n", text);
        // obsolete too
        return String.format("<div align=\"center\">\n\n%s\n\n</div>\n", text);
    }

    public static String image(String imageUrl, String altText) {
        return String.format("![%s](%s)", altText, imageUrl);
    }

    public static String heading1(String text) {
        return String.format("# %s", text);
    }

    public static String heading2(String text) {
        return String.format("## %s", text);
    }

    public static String heading3(String text) {
        return String.format("### %s", text);
    }

    public static String heading4(String text) {
        return String.format("#### %s", text);
    }

    public static String bold(String text) {
        return String.format("**%s**", text);
    }

    public static String horizontalLine() {
        return "---";
    }
}
