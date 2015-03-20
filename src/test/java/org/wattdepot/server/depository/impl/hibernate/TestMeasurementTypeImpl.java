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

package org.wattdepot.server.depository.impl.hibernate;

import org.junit.Test;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;

import static org.junit.Assert.*;

public class TestMeasurementTypeImpl {

  @Test
  public void testEquals() throws Exception {
    MeasurementTypeImpl impl1 = InstanceFactory.getMeasurementType();
    assertTrue(impl1.equals(impl1));
    assertFalse(impl1.equals(null));
    assertFalse(impl1.equals(new Object()));
    assertFalse(impl1.equals("foo"));
    MeasurementTypeImpl impl2 = new MeasurementTypeImpl();
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setId("foo");
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setId(impl1.getId());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setName("foo");
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setName(impl1.getName());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setUnits("foo");
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setUnits(impl1.getUnits());
    assertTrue(impl2.hashCode() == impl1.hashCode());
    assertTrue(impl2.equals(impl1));
    assertTrue(impl2.toString().equals(impl1.toString()));
  }
}