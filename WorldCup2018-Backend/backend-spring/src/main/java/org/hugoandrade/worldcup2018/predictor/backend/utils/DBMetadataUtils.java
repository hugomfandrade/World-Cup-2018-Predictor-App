package org.hugoandrade.worldcup2018.predictor.backend.utils;

import org.hugoandrade.worldcup2018.predictor.backend.league.LeaguesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public final class DBMetadataUtils {

    private DBMetadataUtils() {}

    @Autowired
    private DataSource dataSource;

    class GetTableNames implements DatabaseMetaDataCallback {

        public Object processMetaData(DatabaseMetaData dbmd) throws SQLException {
            ResultSet rs = dbmd
                    // .getSchemas();
                    .getTables(null, null, "%", null);
            // .getTables(dbmd.getUserName(), null, null, new String[]{"TABLE"});
            ArrayList l = new ArrayList();
            while (rs.next()) {

                // System.out.println("   "+rs.getString("TABLE_SCHEM") + ", "+rs.getString("TABLE_CATALOG"));
                l.add(rs.getString(3));
            }

            ResultSet resultSet =
                    dbmd.getColumns(null, null, "ACCOUNT", null);

            while (resultSet.next()) {
                String name = resultSet.getString("COLUMN_NAME");
                String type = resultSet.getString("TYPE_NAME");
                int size = resultSet.getInt("COLUMN_SIZE");

                System.out.println("Column name: [" + name + "]; " +
                        "type: [" + type + "]; size: [" + size + "]");
            }
            return l;
        }
    }

    void run() {

        GetTableNames getTableNames = new GetTableNames();
        try {
            Object o = JdbcUtils.extractDatabaseMetaData(dataSource, getTableNames);
            System.out.println(o);
        } catch (
                MetaDataAccessException e) {
            System.out.println(e);
        }
    }
}
