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

package org.wattdepot.extension.openeis.server;

import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.util.GvizHelper;
import org.wattdepot.extension.openeis.http.api.LongitudinalBenchmarkingGvizResource;
import org.wattdepot.extension.openeis.util.OpenEISGvizHelper;

/**
 * LongitudinalBenchmarkingGvisServerResource - ServerResource that handles GET requests for OpenEIS Longitudinal Benchmarks.
 * @author Cam Moore
 * Created by carletonmoore on 4/18/15.
 */
public class LongitudinalBenchmarkingGvisServerResource extends LongitudinalBenchmarkingServer implements LongitudinalBenchmarkingGvizResource {
  /**
   * The GViz tqx query string.
   */
  private String tqxString = null;

  /**
   * The GViz tq query string.
   */
  private String tqString = null;

  /**
   * Initialize with attributes from the Request.
   */
  @Override
  protected void doInit() {
    super.doInit();

    this.tqxString = GvizHelper.getGvizQueryString(this, "tqx");
    this.tqString = GvizHelper.getGvizQueryString(this, "tq");
  }

  @Override
  public String retrieve() {
    InterpolatedValueList mList = doRetrieve();
    if (mList != null) {
      return OpenEISGvizHelper.getBenchmarkGvizResponse(mList, tqxString, tqString);
    }
    return null;
  }
}