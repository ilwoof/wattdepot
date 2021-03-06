/**
 * UniqueIdException.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
 *
 * This program is free software: you can redistribute it and/or modify
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
package org.wattdepot.common.exception;

/**
 * MissMatchedOwnerException thrown when an object is defined with a different
 * owner than the owner of its components.
 * 
 * @author Cam Moore
 * 
 */
public class MisMatchedOwnerException extends Exception {

  /** Serial Version ID. */
  private static final long serialVersionUID = -7949771009371995775L;

  /** Default Constructor. */
  public MisMatchedOwnerException() {
    super();
  }

  /**
   * @param message
   *          A String message about the exception.
   * @param cause
   *          The Throwable cause of the exception.
   */
  public MisMatchedOwnerException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   *          A String message about the exception.
   */
  public MisMatchedOwnerException(String message) {
    super(message);
  }

  /**
   * @param cause
   *          The Throwable cause of the exception.
   */
  public MisMatchedOwnerException(Throwable cause) {
    super(cause);
  }

}
