/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

/**
 *
 * @author benja
 */
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.image.Image;

public class UserImageLoader {

    /**
     * Loads the user's profile image from the database using the provided userId.
     * If no image is found or there is an error, returns the default image.
     *
     * @param userId the id of the user whose image should be loaded
     * @return an Image object representing the user's profile image
     */
    public static Image loadUserImage(int userId) {
        try {
            // Obtain the connection via your Myconnection singleton.
            Myconnection db = Myconnection.getInstance();
            Connection con = db.getdbconnection();

            // Query to select the profileImage field.
            String sql = "SELECT profileImage FROM users WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                byte[] imgBytes = rs.getBytes("profileImage");

                // If the byte array has data, return an Image built from it.
                if (imgBytes != null && imgBytes.length > 0) {
                    return new Image(new ByteArrayInputStream(imgBytes));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally log the error or notify the user.
        }

        // Return a default image if no image is found or if an error occurred.
        return new Image(UserImageLoader.class.getResourceAsStream("/images/default_profile.webp"));
    }
}
