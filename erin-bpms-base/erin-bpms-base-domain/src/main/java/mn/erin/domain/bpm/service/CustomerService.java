/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.service;

import java.util.List;
import java.util.Map;

import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.property.PropertyInfo;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleOwner;

/**
 * Represents customer specific services.
 *
 * @author EBazarragchaa
 */
public interface CustomerService
{
  /**
   * Gets customer salary information.
   *
   * @param regNumber           Uniques person id.
   * @param employeeRegNumber   Uniques person id of employee.
   * @return {@link AddressInfo} customer address.
   * @throws BpmServiceException when this service is not reachable or usable.
   */
  AddressInfo getCustomerAddress(String regNumber, String employeeRegNumber)
      throws BpmServiceException;

  /**
   * Gets customer info by following parameters.
   *
   * @param regNumber           Uniques person id.
   * @param employeeRegNumber   Uniques person id of employee.
   * @return {@link Customer} customer.
   * @throws BpmServiceException when this service is not reachable or usable.
   */
  Customer getCustomerInfo(String regNumber, String employeeRegNumber) throws BpmServiceException;

  /**
   * Gets customer salary info by following parameters.
   *
   * @param regNumber           Uniques person id.
   * @param employeeRegNumber   Uniques person id of employee.
   * @param month               month of salary.
   * @return {@link SalaryInfo}s salary info.
   * @throws BpmServiceException when this service is not reachable or usable.
   */
  List<SalaryInfo> getCustomerSalaryInfos(String regNumber, String employeeRegNumber, Integer month)
      throws BpmServiceException;

  /**
   * Gets all collateral by customer cif number.
   *
   * @param operatorInfo Map ofoperator info required to get property information.
   * @param citizenInfo  Map of citizen info required to get property information.
   * @return Property info.
   * @throws BpmServiceException when this service is not reachable or usable.
   */
  PropertyInfo getPropertyInfo(Map<String, String> operatorInfo, Map<String, String> citizenInfo, String propertyId) throws BpmServiceException;

  /**
   * Gets customer vehicle info depending on plate number.
   *
   * @param regNumber           Uniques person id.
   * @param employeeRegNumber   Uniques person id of employee.
   * @param plateNumber         Unique id of vehicle plate.
   * @return found {@link VehicleInfo}.
   * @throws BpmServiceException
   */
  VehicleInfo getCustomerVehicleInfo(String regNumber, String employeeRegNumber, String plateNumber)
      throws BpmServiceException;


  /**
   * Gets customer vehicle owner infos depending on plate number.
   *
   * @param regNumber           Uniques person id.
   * @param employeeRegNumber   Uniques person id of employee.
   * @param plateNumber         Unique id of vehicle plate.
   * @return found {@link VehicleOwner list}.
   * @throws BpmServiceException
   */
  List<VehicleOwner> getCustomerVehicleOwners(String regNumber, String employeeRegNumber, String plateNumber)
      throws BpmServiceException;
}
