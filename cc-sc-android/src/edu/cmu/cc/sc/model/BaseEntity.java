/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.model;

import java.io.Serializable;

/**
 * DESCRIPTION: Base entity class for all entity classes.
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 4, 2013
 */
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = 3L;
	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	/** Entity id */
	protected long id;

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@Override
	public int hashCode() {
		return (int) id;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj != null) {
			if (getClass().equals(obj.getClass())) {
				BaseEntity anotherEntity = (BaseEntity) obj;
				return (id == anotherEntity.getId());
			}
		}

		return false;
	}

}
