/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.oasis_open.docs.wsrf.rp_2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}Insert"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}Update"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}Delete"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "insertOrUpdateOrDelete"
})
@XmlRootElement(name = "SetResourceProperties")
public class SetResourceProperties {

    @XmlElements({
        @XmlElement(name = "Insert", type = InsertType.class),
        @XmlElement(name = "Update", type = UpdateType.class),
        @XmlElement(name = "Delete", type = DeleteType.class)
    })
    protected List<Object> insertOrUpdateOrDelete;

    /**
     * Gets the value of the insertOrUpdateOrDelete property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the insertOrUpdateOrDelete property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInsertOrUpdateOrDelete().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InsertType }
     * {@link UpdateType }
     * {@link DeleteType }
     * 
     * 
     */
    public List<Object> getInsertOrUpdateOrDelete() {
        if (insertOrUpdateOrDelete == null) {
            insertOrUpdateOrDelete = new ArrayList<Object>();
        }
        return this.insertOrUpdateOrDelete;
    }

}
