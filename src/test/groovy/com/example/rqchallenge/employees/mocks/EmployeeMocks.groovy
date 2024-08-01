package com.example.rqchallenge.employees.mocks

class EmployeeMocks {
    static String getAllEmployeeEmptyResponse() {
        return "{\n" +
                "\t\"status\": \"success\",\n" +
                "\t\"data\": [],\n" +
                "\t\"message\": \"No Employee found list!\"\n" +
                "}"
    }

    static String getAllEmployeeResponse() {
        return "{\n" +
                "\t\"status\": \"success\",\n" +
                "\t\"data\": [\n" +
                "\t\t{\n" +
                "\t\t\"id\": \"1\",\n" +
                "\t\t\"employee_name\": \"Tiger Nixon\",\n" +
                "\t\t\"employee_salary\": \"320800\",\n" +
                "\t\t\"employee_age\": \"61\",\n" +
                "\t\t\"profile_image\": \"\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"message\": \"Employee list successfully returned!\"\n" +
                "}"
    }

    static String getEmployeeByIdNotFound() {
        return "{\n" +
                "    \"status\": \"success\",\n" +
                "    \"data\": null,\n" +
                "    \"message\": \"Successfully! Record has been fetched.\"\n" +
                "}"
    }

    static String getEmployeeByIdFound() {
        return "{\n" +
                "    \"status\": \"success\",\n" +
                "    \"data\": {\n" +
                "        \"id\": 1,\n" +
                "        \"employee_name\": \"Tiger Nixon\",\n" +
                "        \"employee_salary\": 320800,\n" +
                "        \"employee_age\": 61,\n" +
                "        \"profile_image\": \"\"\n" +
                "    },\n" +
                "    \"message\": \"Successfully! Record has been fetched.\"\n" +
                "}"
    }

    static Map<String, Object> getEmployeeRequest() {
        Map<String, Object> reqObj = new HashMap<>()
        reqObj.put('name', 'Joe Tester')
        reqObj.put('salary', 35000)
        reqObj.put('age', 35)
        return reqObj
    }

    static String getCreateEmployeeResponse() {
        return "{\n" +
                "\t\"status\":\"success\",\n" +
                "\t\"data\":\n" +
                "\t\t{\n" +
                "\t\t\t\"name\":\"Joe Tester\",\n" +
                "\t\t\t\"salary\":35000,\n" +
                "\t\t\t\"age\":35,\n" +
                "\t\t\t\"id\":4111\n" +
                "\t\t}\n" +
                "\t,\"message\":\"Successfully! Record has been added.\"\n" +
                "}"
    }

    static String getDeleteEmployeeResponse() {
        return "{\n" +
                "\t\"status\":\"success\",\n" +
                "\t\"data\": \"25\",\n" +
                "\t,\"message\":\"Successfully! Record has been deleted.\"\n" +
                "}"
    }

}
