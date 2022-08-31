/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.metadata;
/**
 * Parent of all metadata of objects: they all belong to a catalog and schema.
 * @author Nicolas de Pomereu
 *
 */

public class CatalogAndSchema {

    private String catalog = "";
    private String schema = "";

    public String getCatalog() {
        return catalog;
    }
    void setCatalog(String catalog) {
        this.catalog = catalog;
    }
    public String getSchema() {
        return schema;
    }
    void setSchema(String schema) {
        this.schema = schema;
    }
}
