/*
 * Copyright (C) 2020 claireprudhomme.
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
package info.ponciano.lab.geotimewfs.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author claireprudhomme
 */
@RestController
@RequestMapping("/api/geotimeWFS")
public class OGCapiRecordsController {

    /**
     * The landing page provides links to the API definition, links to the
     * conformance statement, links to catalogues metadata and links to other
     * resources offered by the service.
     *
     * @param f A MIME type indicating the representation of the resources to be
     * presented. Available values : json, xml, html
     * @param model
     * @return
     */
    @GetMapping("/")
    public String getLandingPage(@RequestParam(name = "f", required = false, defaultValue = "html") String f, Model model) {
        return "";
    }

    /**
     * The conformance provides the conformance statement
     *
     * @param f A MIME type indicating the representation of the resources to be
     * presented. Available values : json, xml, html
     * @param model
     * @return
     */
    @GetMapping("/conformance")
    public String getConformance(@RequestParam(name = "f", required = false, defaultValue = "html") String f, Model model) {
        return "";
    }

    /**
     * A catalogue is a collection of records that describe a set of things. A
     * catalogue end point may may offer a single collection of records (the
     * usual case) but may offer more that one collection of records each
     * describing different things (e.g. a catalogue of imagery and a catalogue
     * of vector data). The /collections endpoint provides metadata about the
     * list of available record collections.
     *
     * @param f A MIME type indicating the representation of the resources to be
     * presented. Available values : json, xml, html
     * @param model
     * @return
     */
    @Operation(summary = "The set of catalogues offered at this endpoint.", 
            description = "A catalogue is a collection of records that describe a set of things. A catalogue end point may may offer a single collection of records (the usual case) but may offer more that one collection of records each describing different things (e.g. a catalogue of imagery and a catalogue of vector data). The /collections endpoint provides metadata about the list of available record collections.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OGCapiRecordsController.class))}),
        @ApiResponse(responseCode = "default", 
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OGCapiRecordsController.class))})})
    @GetMapping("/collections")
    public String getCatalogues(@RequestParam(name = "f", required = false, defaultValue = "html") String f, Model model) {
        return "";
    }

    /**
     * Provides metadata about a specific collection of records. 
     * Same output as generated for /collections but specific to the indicated catalogueId
     * 
     * @param catalogueId Identifier of a catalogue offered by the service.
     * Available values : ogcCore, ebRIM
     * @param f A MIME type indicating the representation of the resources to be
     * presented (e.g. application/xml). Available values : html, xml, json
     * @param model
     * @return
     */
    @Operation(description = "Provides metadata about a specific collection of records. Same output as generated for /collections but specific to the indicated catalogueId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the book",
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OGCapiRecordsController.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Book not found",
                content = @Content)})     
    @GetMapping("/collections/{catalogueId}")
    public String getCatalogue(@RequestParam(name = "catalogueId", required = true) @PathVariable String catalogueId, @RequestParam(name = "f", required = false, defaultValue = "html") String f, Model model) {
        return "";
    }

    @Operation(summary = "Get the list of queryables for this catalogue")
    @GetMapping("/collections/{catalogueId}/queryables")
    public String getQueryables(@RequestParam(name = "catalogueId", required = true) @PathVariable String catalogueId, @RequestParam(name = "f", required = false, defaultValue = "html") String f, Model model) {
        return "";
    }

    @GetMapping("/collections/{catalogueId}/items")
    public String getRecords(
            @RequestParam(name = "catalogueId", required = true) @PathVariable String catalogueId, 
            @RequestParam(name = "f", required = false, defaultValue = "html") String f,
            @RequestParam(name = "crs", required = false, defaultValue = "") String crs,
            @RequestParam(name = "offset", required = false, defaultValue = "html") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "html") int limit,
            @RequestParam(name = "q", required = false, defaultValue = "html") String q,
            @RequestParam(name = "bbox", required = false, defaultValue = "html") String bbox,
            @RequestParam(name = "geometry", required = false, defaultValue = "html") String geometry,
            @RequestParam(name = "geometry_crs", required = false, defaultValue = "html") String geometry_crs,
            @RequestParam(name = "gRelation", required = false, defaultValue = "html") String gRelation,
            @RequestParam(name = "lat", required = false, defaultValue = "html") double lat,
            @RequestParam(name = "lon", required = false, defaultValue = "html") double lon,
            @RequestParam(name = "radius", required = false, defaultValue = "html") double radius,
            @RequestParam(name = "time", required = false, defaultValue = "html") String time,
            @RequestParam(name = "tRelation", required = false, defaultValue = "html") String tRelation,
            @RequestParam(name = "filter", required = false, defaultValue = "html") String filter,
            @RequestParam(name = "filter_language", required = false, defaultValue = "html") String filter_language,
            Model model) {
        return "";
    }

    @GetMapping("/collections/{catalogueId}/items/{recordId}")
    public String getRecord(@RequestParam(name = "catalogueId", required = true) @PathVariable("catalogueId") String catalogueId, @RequestParam(name = "recordId", required = true) @PathVariable("recordId") String recordId,@RequestParam(name = "f", required = false, defaultValue = "html") String f, Model model) {
        return "";
    }
}
