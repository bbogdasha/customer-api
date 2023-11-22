import {
    Alert,
    AlertIcon,
    Box,
    Button,
    Flex,
    FormLabel,
    Heading,
    Image,
    Input,
    Link,
    Stack,
    Text,
    useColorModeValue,
} from '@chakra-ui/react'
import {Formik, Form, useField} from "formik";
import * as Yup from 'yup';
import { useAuth } from '../context/AuthContext';
import { errorNotification } from '../../services/notification';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

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

const LoginForm = () => {

    const {login} = useAuth()

    const navigate = useNavigate()

    return (
        <Box
          rounded={'lg'}
          bg={useColorModeValue('white', 'gray.700')}
          boxShadow={'lg'}
          p={8}>
            <Stack spacing={4}>
                <Formik
                    validateOnMount={true}
                    validationSchema={
                        Yup.object({
                            username: Yup.string()
                                .email("Must be valid email")
                                .required("Email is required"),
                            password: Yup.string()
                                .max(20, "Password cannot be more than 20 characters")
                                .required("Password is required")
                        })
                    }
                    initialValues={{username: '', password: ''}}
                    onSubmit={(values, {setSubmitting}) => {
                        setSubmitting(true)
                        login(values).then(res => {
                            navigate("/dashboard")
                            console.log("Success login")
                        }).catch(err => {
                            errorNotification(
                                err.response.data.error,
                                err.response.data.message
                            )
                        }).finally(() => {
                            setSubmitting(false)
                        })
                    }}
                >

                    {(isValid, isSubmitting) => {
                        return (
                            <Form>
                                <Stack spacing={8}>
                                    <MyTextInput
                                        label={"Email address"}
                                        name={"username"}
                                        type={"email"}
                                        placeholder={"myEmail@email.com"}
                                    >
                                    </MyTextInput>
                                    <MyTextInput
                                        label={"Password"}
                                        name={"password"}
                                        type={"password"}
                                        placeholder={"Type your password"}
                                    ></MyTextInput>

                                    <Button 
                                        bg={'blue.400'}
                                        color={'white'}
                                        _hover={{
                                            bg: 'blue.500',
                                        }}
                                        disabled={!isValid || isSubmitting}
                                        type={"submit"}
                                    >
                                        Sign in
                                    </Button>
                                </Stack>
                            </Form>
                        )
                    }}

                </Formik>
            </Stack>
        </Box>
    )
}
  
const Login = () => {

    const {customer} = useAuth()

    const navigate = useNavigate()

    useEffect(() => {
        if(customer) {
            navigate("/dashboard")
        }
    })

    return (
        <Flex
            minH={'100vh'}
            align={'center'}
            justify={'center'}
            bg={useColorModeValue('gray.50', 'gray.800')}>
            <Stack spacing={8} mx={'auto'} maxW={'lg'} py={12} px={6}>
                <Stack align={'center'}>
                    <Heading fontSize={'4xl'}>Sign in to your account</Heading>
                    <Text fontSize={'lg'} color={'gray.600'}>
                        to enjoy all of our cool features ✌️
                    </Text>
                </Stack>
                <LoginForm>

                </LoginForm>
                <Link color={'blue.500'} href={"/signup"}>
                    Don't have an account? Signup now.
                </Link>
            </Stack>
        </Flex>
    )
}

export default Login;