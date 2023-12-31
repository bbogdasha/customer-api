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
import { saveCustomer } from '../../services/client';
import { errorNotification, successNotification } from '../../services/notification';

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

const MySelect = ({ label, ...props }) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon></AlertIcon>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const CreateCustomerForm = ({onSuccess}) => {
    return (
    <>
        <Formik
            initialValues={{
                name: '',
                email: '',
                age: 16, 
                gender: '',
                password: ''
            }}
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
                    .required('Required'),
                gender: Yup.string()
                    .oneOf(['MALE', 'FEMALE'], 'Invalid gender type')
                    .required('Required'),
                password: Yup.string()
                    .min(8, 'Must be 8 characters or more')
                    .max(20, 'Must be 20 characters or less')
            })}
            
            onSubmit={(customer, { setSubmitting }) => {
                setSubmitting(true)
                saveCustomer(customer).then(res => {
                    successNotification(
                        "Customer saved.",
                        `${customer.name} was successfully saved!`
                    )
                    console.log(res)
                    onSuccess(res.headers["authorization"])
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

            {({isValid, isSubmitting}) => (
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

                        <MyTextInput
                            label="Password"
                            name="password"
                            type="password"
                            placeholder="Customer`s password"
                        />

                        <MySelect label="Gender" name="gender">
                            <option value="">Select a gender type</option>
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                        </MySelect>

                        <Button disabled={!isValid || isSubmitting} type="submit">Submit</Button>
                    </Stack>
                </Form>
            )}

        </Formik>
    </>
    );
};

export default CreateCustomerForm