/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.client.http.api.csv;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementListSizeExceededException;
import org.wattdepot.common.exception.MeasurementTypeException;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.Date;

/**
 * Imports an Organization's data that has been previously exported.  The user and organization must be defined in advance.
 *
 * @author Cam Moore
 */
public class ImportOrganizationData {

  /**
   * Command line interface.
   *
   * @param args command line arguments -s <server uri> -u <username> -p
   *             <password> -o <orgId> -f <fileName> [-d].
   * @throws BadSensorUriException                if there is a problem with the WattDepot
   *                                              sensor definition.
   * @throws IdNotFoundException                  if there is a problem with the organization id.
   * @throws BadCredentialException               if the credentials are not valid.
   * @throws IOException                          if there is a problem with reading the file.
   * @throws java.text.ParseException             if there is a problem with the date format.
   * @throws DatatypeConfigurationException       if there is an internal issue.
   * @throws MeasurementTypeException             if there is a problem between the depository and measurements.
   * @throws MeasurementListSizeExceededException Should not happen.
   */
  public static void main(String[] args) throws BadCredentialException, IdNotFoundException, BadSensorUriException, IOException, java.text.ParseException, DatatypeConfigurationException, MeasurementTypeException, MeasurementListSizeExceededException {
    Options options = new Options();
    CommandLine cmd = null;
    String serverUri = null;
    String username = null;
    String organizationId = null;
    String password = null;
    String fileName = null;
    boolean debug = false;

    options.addOption("h", false,
        "Usage: SaveOrganizationDomainMain -s <server uri> -u <username>"
            + " -p <password> -o <orgId> -f <fileName> [-d]");
    options.addOption("s", "server", true,
        "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
    options.addOption("f", "fileName", true, "Domain definition file name.");
    options.addOption("d", "debug", false,
        "Displays debugging information.");

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    try {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e) {
      System.err.println("Command line parsing failed. Reason: "
          + e.getMessage() + ". Exiting.");
      System.exit(1);
    }
    if (cmd.hasOption("h")) {
      formatter.printHelp("LoadOrganizationDomainMain", options);
      System.exit(0);
    }
    if (cmd.hasOption("s")) {
      serverUri = cmd.getOptionValue("s");
    }
    else {
      serverUri = "http://server.wattdepot.org/";
    }
    if (cmd.hasOption("u")) {
      username = cmd.getOptionValue("u");
    }
    else {
      username = "user";
    }
    if (cmd.hasOption("p")) {
      password = cmd.getOptionValue("p");
    }
    else {
      password = "default";
    }
    if (cmd.hasOption("o")) {
      organizationId = cmd.getOptionValue("o");
    }
    else {
      organizationId = "organization";
    }
    if (cmd.hasOption("f")) {
      fileName = cmd.getOptionValue("f");
    }
    else {
      fileName = "organization.csv";
    }
    debug = cmd.hasOption("d");
    if (debug) {
      System.out.println("Organization Domain Main:");
      System.out.println("    WattDepotServer: " + serverUri);
      System.out.println("    Username: " + username);
      System.out.println("    OrganizationId: " + organizationId);
      System.out.println("    Password: ********");
      System.out.println("    fileName: " + fileName);
    }
    OrganizationDomain od = new OrganizationDomain(serverUri, username,
        organizationId, password, fileName);
    Date start = new Date();
    Integer count = od.importData();
    Date end = new Date();
    Long seconds = (end.getTime() - start.getTime()) / 1000;
    System.out.println("It took " + seconds + " to load " + count + " items into WattDepot at " + serverUri);
  }
}