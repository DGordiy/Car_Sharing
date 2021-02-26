package carsharing;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    static final Scanner SCANNER = new Scanner(System.in);
    static CompanyDAO companyDAO;
    static CarDAO carDAO;
    static CustomerDAO customerDAO;

    public static void main(String[] args) throws ClassNotFoundException {
        DBEngine dbEngine = new DBEngine(args.length > 1 && args[0].equals("-databaseFileName") ? args[1] : "carsharing");

        companyDAO = new CompanyDAO(dbEngine);
        carDAO = new CarDAO(dbEngine);
        customerDAO = new CustomerDAO(dbEngine);

        try {
            companyDAO.createTable();
        } catch (SQLException ignored) {}

        try {
            carDAO.createTable();
        } catch (SQLException ignored) {}
        try {
            customerDAO.createTable();
        } catch (SQLException ignored) {}

        mainMenu();
        SCANNER.close();
    }

    public static void mainMenu() {
        System.out.println();
        System.out.println("1. Log in as a manager");
        System.out.println("2. Log in as a customer");
        System.out.println("3. Create a customer");
        System.out.println("0. Exit");

        switch (SCANNER.nextLine().trim()) {
            case "1":
                logInAsManager();
                break;
            case "2":
                logInAsCustomer();
                break;
            case "3":
                createCustomer();
                break;
            case "0":
                return;
            default:
                System.out.println("Incorrect selection");
        }

        mainMenu();
    }

    public static void logInAsManager() {
        managerMenu();
    }

    public static void managerMenu() {
        System.out.println();
        System.out.println("1. Company list");
        System.out.println("2. Create a company");
        System.out.println("0. Back");

        switch (SCANNER.nextLine().trim()) {
            case "1":
                showCompanyList(null);
                break;
            case "2":
                createCompany();
                break;
            case "0":
                return;
            default:
                System.out.println("Incorrect selection");
        }

        managerMenu();
    }

    //If customer == null it is manager menu else customer menu for rent car
    private static void showCompanyAction(Company company, Customer customer) {
        System.out.println();
        if (customer == null) {
            System.out.format("'%s' company\n", company.getName());
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            System.out.println("0. Back");

            switch (SCANNER.nextLine().trim()) {
                case "1":
                    showCarList(company);
                    break;
                case "2":
                    createCar(company);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Incorrect selection");
            }

            showCompanyAction(company, null);
        } else {
            try {
                List<Car> cars = companyDAO.findFreeCars(company, customerDAO, carDAO);
                if (!cars.isEmpty()) {
                    showCarSelect(cars, customer);
                } else {
                    System.out.format("No available cars in the '%s' company\n", company.getName());
                    showCustomerAction(customer);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showCompanyList(Customer customer) {
        System.out.println();
        try {
            List<Company> companies = companyDAO.findAll();

            if (companies.isEmpty()) {
                System.out.println("The company list is empty!");
            } else {
                showCompanySelect(companies, customer);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void createCompany() {
        System.out.println();
        System.out.println("Enter the company name:");
        String name = SCANNER.nextLine();
        try {
            companyDAO.save(new Company(name));

            System.out.println("The company was created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showCompanySelect(List<Company> companies, Customer customer) {
        System.out.println("Choose a company:");

        Map<Integer, Company> selectionIndex = new HashMap<>();
        int i = 0;
        for (Company company : companies) {
            System.out.format("%d. %s\n", ++i, company.getName());
            selectionIndex.put(i, company);
        }

        System.out.println("0. Back");

        int selection = Integer.parseInt(SCANNER.nextLine());
        if (selection == 0) {
            return;
        }

        if (selectionIndex.containsKey(selection)) {
            showCompanyAction(selectionIndex.get(selection), customer);
        } else {
            System.out.println("Incorrect selection\n");
            showCompanySelect(companies, customer);
        }
    }

    public static void showCarList(Company company) {
        System.out.println();
        try {
            List<Car> cars = carDAO.findByCompany(company);

            if (cars.isEmpty()) {
                System.out.println("The car list is empty!");
            } else {
                System.out.format("'%s' cars:\n", company.getName());
                int i = 0;
                for (Car car : cars) {
                    System.out.format("%d. %s\n", ++i, car.getName());
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void showCarSelect(List<Car> cars, Customer customer) {
        System.out.println("Choose a car:");

        Map<Integer, Car> selectionIndex = new HashMap<>();
        int i = 0;
        for (Car car : cars) {
            System.out.format("%d. %s\n", ++i, car.getName());
            selectionIndex.put(i, car);
        }

        System.out.println("0. Back");

        int selection = Integer.parseInt(SCANNER.nextLine());
        if (selection == 0) {
            return;
        }

        if (selectionIndex.containsKey(selection)) {
            Car car = selectionIndex.get(selection);
            //update data in DB
            try {
                customerDAO.updateRentCarId(customer, car.getId());
                customer.setRented_car_id(car.getId());
                System.out.format("You rented '%s'\n", car.getName());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //showCustomerAction(customer);
        } else {
            System.out.println("Incorrect selection\n");
            showCarSelect(cars, customer);
        }
    }

    public static void createCar(Company company) {
        System.out.println();
        System.out.println("Enter the car name:");
        String name = SCANNER.nextLine();
        try {
            carDAO.save(new Car(name, company.getId()));

            System.out.println("The car was added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logInAsCustomer() {
        customerMenu();
    }

    public static void customerMenu() {
        System.out.println();
        try {
            List<Customer> customers = customerDAO.findAll();

            if (customers.isEmpty()) {
                System.out.println("The customer list is empty!");
            } else {
                showCustomerSelect(customers);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void createCustomer() {
        System.out.println();
        System.out.println("Enter the customer name:");
        String name = SCANNER.nextLine();
        try {
            customerDAO.save(new Customer(name));

            System.out.println("The customer was added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showCustomerSelect(List<Customer> customers) {
        System.out.println("Choose a customer:");

        Map<Integer, Customer> selectionIndex = new HashMap<>();
        int i = 0;
        for (Customer customer : customers) {
            System.out.format("%d. %s\n", ++i, customer.getName());
            selectionIndex.put(i, customer);
        }

        System.out.println("0. Back");

        int selection = Integer.parseInt(SCANNER.nextLine());
        if (selection == 0) {
            return;
        }

        if (selectionIndex.containsKey(selection)) {
            Customer customer = selectionIndex.get(selection);
            showCustomerAction(customer);
        } else {
            System.out.println("Incorrect selection\n");
            showCustomerSelect(customers);
            return;
        }
    }

    public static void showCustomerAction(Customer customer) {
        System.out.println();
        System.out.println("1. Rent a car");
        System.out.println("2. Return a rented car");
        System.out.println("3. My rented car");
        System.out.println("0. Back");

        switch (SCANNER.nextLine().trim()) {
            case "1":
                rentCar(customer);
                break;
            case "2":
                returnCar(customer);
                break;
            case "3":
                showRentedCar(customer);
                break;
            case "0":
                return;
            default:
                System.out.println("Incorrect selection");
        }

        showCustomerAction(customer);
    }

    public static void rentCar(Customer customer) {
        if (customer.getRented_car_id() != 0) {
            System.out.println("You've already rented a car!");
            return;
        }

        showCompanyList(customer);
    }

    public static void returnCar(Customer customer) {
        if (customer.getRented_car_id() == 0) {
            System.out.println("You didn't rent a car!");
            return;
        }

        try {
            customerDAO.updateRentCarId(customer, 0);
            customer.setRented_car_id(0);

            System.out.println("You've returned a rented car!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showRentedCar(Customer customer) {
        if (customer.getRented_car_id() == 0) {
            System.out.println("You didn't rent a car!");
            return;
        }

        try {
            Car car = carDAO.findById(customer.getRented_car_id());

            System.out.println("Your rented car:");
            System.out.println(car.getName());
            System.out.println("Company:");
            System.out.println(companyDAO.findById(car.getCompanyId()).getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}