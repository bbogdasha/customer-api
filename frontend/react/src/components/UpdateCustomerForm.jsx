import { 
    Alert, 
    AlertIcon, 
    Box, 
    FormLabel, 
    Input, 
    Select, 
    Button, 
    Stack 
} from '@chakra-ui/react';
import { 
    Formik, 
    Form, 
    useField,
} from 'formik';
import * as Yup from 'yup';
import { updateCustomer } from '../services/client';
import { errorNotification, successNotification } from '../services/notification';

const MyTextInput = ({ label, ...props }) => {

    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon></AlertIcon>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const UpdateCustomerForm = ({fetchCustomers, initialValues, customerId}) => {
    return (
    <>
        <Formik
            initialValues={initialValues}
            validationSchema={Yup.object({
                name: Yup.string()
                    .max(15, 'Must be 15 characters or less')
                    .required('Required'),
                email: Yup.string()
                    .email('Invalid email address')
                    .required('Required'),
                age: Yup.number()
                    .min(16, 'Must be at least 16 years of age')
                    .max(100, 'Must be less than 100 years of age')
                    .required('Required')
            })}
            
            onSubmit={(updatedCustomer, { setSubmitting }) => {
                setSubmitting(true)
                updateCustomer(customerId, updatedCustomer).then(res => {
                    console.log(res)
                    successNotification(
                        "Customer updated.",
                        `${updatedCustomer.name} was successfully updated!`
                    )
                    fetchCustomers()
                }).catch(err => {
                    console.log(err)
                    errorNotification(
                        err.response.data.error,
                        err.response.data.message
                    )
                }).finally(() => {
                    setSubmitting(false)
                })
            }}
        >

            {({isValid, isSubmitting, dirty}) => (
                <Form>
                    <Stack spacing={"24px"}>
                        <MyTextInput
                            label="Name"
                            name="name"
                            type="text"
                            placeholder="Jane"
                        />

                        <MyTextInput
                            label="Email Address"
                            name="email"
                            type="email"
                            placeholder="jane@formik.com"
                        />

                        <MyTextInput
                            label="Age"
                            name="age"
                            type="number"
                            placeholder="27"
                        />

                        <Button disabled={!(isValid && dirty) || isSubmitting} type="submit">Submit</Button>
                    </Stack>
                </Form>
            )}

        </Formik>
    </>
    );
};

export default UpdateCustomerForm