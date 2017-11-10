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
package org.oasis_open.docs.wsdm.muws2_2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;
import javax.xml.namespace.QName;
import org.oasis_open.docs.wsdm.mows_2.DownState;
import org.oasis_open.docs.wsdm.mows_2.OperationalStateType;
import org.oasis_open.docs.wsdm.mows_2.UpState;
import org.w3c.dom.Element;

/**
 * <p>Java class for StateTransitionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType name="StateTransitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/muws2-2.xsd}EnteredState"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/muws2-2.xsd}PreviousState" minOccurs="0"/>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="TransitionIdentifier" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="Time" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StateTransitionType", propOrder = {
    "enteredState",
    "previousState",
    "any"
})
@XmlSeeAlso({OperationalStateType.class, StateType.class,
    UpState.class,
    DownState.class,
    CategoryType.class})
public class StateTransitionType {

    @XmlElement(name = "EnteredState", required = true)
    protected StateType enteredState;
    @XmlElement(name = "PreviousState")
    protected StateType previousState;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "TransitionIdentifier")
    @XmlSchemaType(name = "anyURI")
    protected String transitionIdentifier;
    @XmlAttribute(name = "Time", required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar time;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the enteredState property.
     *
     * @return possible object is {@link StateType }
     *
     */
    public StateType getEnteredState() {
        return enteredState;
    }

    /**
     * Sets the value of the enteredState property.
     *
     * @param value allowed object is {@link StateType }
     *
     */
    public void setEnteredState(StateType value) {
        this.enteredState = value;
    }

    /**
     * Gets the value of the previousState property.
     *
     * @return possible object is {@link StateType }
     *
     */
    public StateType getPreviousState() {
        return previousState;
    }

    /**
     * Sets the value of the previousState property.
     *
     * @param value allowed object is {@link StateType }
     *
     */
    public void setPreviousState(StateType value) {
        this.previousState = value;
    }

    /**
     * Gets the value of the any property.
     *
     * <p> This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the any property.
     *
     * <p> For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p> Objects of the following type(s) are allowed in the list      {@link Element }
     * {@link Object }
     *
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the transitionIdentifier property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getTransitionIdentifier() {
        return transitionIdentifier;
    }

    /**
     * Sets the value of the transitionIdentifier property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setTransitionIdentifier(String value) {
        this.transitionIdentifier = value;
    }

    /**
     * Gets the value of the time property.
     *
     * @return possible object is {@link Calendar }
     *
     */
    public Calendar getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     *
     * @param value allowed object is {@link Calendar }
     *
     */
    public void setTime(Calendar value) {
        this.time = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed
     * property on this class.
     *
     * <p> the map is keyed by the name of the attribute and the value is the
     * string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute by
     * updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }
}
