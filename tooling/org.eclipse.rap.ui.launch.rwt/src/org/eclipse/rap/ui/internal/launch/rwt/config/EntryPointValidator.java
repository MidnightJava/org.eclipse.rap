/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.config;

import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig.LaunchTarget;


class EntryPointValidator extends Validator {

  static final int ERR_ENTRY_POINT_EMPTY = 8021;

  EntryPointValidator( RWTLaunchConfig config, ValidationResult validationResult ) {
    super( config, validationResult );
  }

  void validate() {
    if( LaunchTarget.ENTRY_POINT.equals( config.getLaunchTarget() ) ) {
      if( config.getEntryPoint().length() == 0 ) {
        addError( "The entry point is empty.", ERR_ENTRY_POINT_EMPTY );
      }
    }
  }
}
