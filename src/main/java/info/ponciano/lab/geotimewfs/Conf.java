/*
 * Copyright (C) 2020 Dr Jean-Jacques Ponciano (Contact: jean-jacques@ponciano.info).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package info.ponciano.lab.geotimewfs;

import info.ponciano.lab.geotimewfs.controllers.examples.semanticwfs.ResController;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Dr Jean-Jacques Ponciano (Contact: jean-jacques@ponciano.info)
 */
public class Conf {
    public static Conf instance;
    protected JSONObject wfsconf;

    private Conf() {
        try {
            this.wfsconf = new JSONObject(new String(Files.readAllBytes(Paths.get("wfsconf.json")), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            Logger.getLogger(ResController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static Conf get(){
        if (instance==null)instance=new Conf();
        return instance;
    }

    public JSONObject getWfsconf() {
        return wfsconf;
    }
    
}