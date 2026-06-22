public class TestImagePath {
    public static void main(String[] args) {
        // Tester diff?rents chemins
        String[] paths = {
            "/images/categories/telephone.jpg",
            "images/categories/telephone.jpg",
            "/images/categories/telephone.png",
            "/images/product-1.png",
            "/images/logo-icon2.png"
        };
        
        for (String path : paths) {
            java.io.InputStream is = TestImagePath.class.getResourceAsStream(path);
            System.out.println((is != null ? "? TROUVE" : "? ABSENT") + " : " + path);
            if (is != null) try { is.close(); } catch (Exception e) {}
        }
    }
}
