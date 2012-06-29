/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.jdbc.applet;

import org.neo4j.jdbc.Driver;
import org.restlet.engine.Engine;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO
 */
public class Neo4jApplet
        extends JApplet
{
    private Connection conn;
    private Logger logger;
    private Driver driver;
    private String url;

    //Called when this applet is loaded into the browser.
    public void init()
    {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        url = super.getParameter("url");
        logger = Logger.getLogger(Neo4jApplet.class.getName());
        logger.info("Initializing Neo4j JDBC Applet");
        try
        {
            Engine.setInstance(new AppletEngine());

            driver = new Driver();
            DriverManager.registerDriver(driver);
            logger.info("Registered Neo4j JDBC Driver");
        } catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error:" + e, e);
        }
    }

    @Override
    public void start()
    {
        setUrl(url);
    }

    @Override
    public void stop()
    {
        try
        {
            conn.close();
            logger.info("Closed connection");
        } catch (SQLException e)
        {
            logger.log(Level.SEVERE, "Error:" + e, e);
        }
    }

    @Override
    public void destroy()
    {
        try
        {
            DriverManager.deregisterDriver(driver);
        } catch (SQLException e)
        {
            logger.log(Level.SEVERE, "Error:" + e, e);
        }
    }

    public void setUrl(String url)
    {
        this.url = url;

        try
        {
            if (conn != null)
            {
                conn.close();
            }

            logger.info("Connecting to " + url);
            conn = DriverManager.getConnection(url);
            logger.info("Connected");
        } catch (SQLException e)
        {
            logger.log(Level.SEVERE, "Error:" + e, e);
        }
    }

    public String getUrl()
    {
        return url;
    }

    public Connection getConnection()
    {
        return conn;
    }

    public ResultSet query(String query) throws SQLException
    {
        Logger.getLogger(Neo4jApplet.class.getName()).info("Sending query:"+query);

        return conn.createStatement().executeQuery(query);
    }

    public class AppletEngine extends Engine
    {
        @Override
        protected ClassLoader createClassLoader()
        {
            return getClass().getClassLoader();
        }
    }
}
