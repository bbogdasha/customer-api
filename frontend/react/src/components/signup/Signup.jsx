import {
    Box,
    Flex,
    Heading,
    Link,
    Stack,
    Text,
    useColorModeValue,
} from '@chakra-ui/react'
import CreateCustomerForm from "../customer/CreateCustomerForm"
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

const Signup = () => {

    const {customer, setCustomerFromToken} = useAuth()

    const navigate = useNavigate()

    useEffect(() => {
        if(customer) {
            navigate("/dashboard/customers")
        }
    })

    return (
        
        <Flex
            align={'center'}
            justify={'center'}
            bg={useColorModeValue('gray.50', 'gray.800')}>
            <Stack spacing={20} mx={'auto'} maxW={'lg'} py={12} px={6}>
                <Stack align={'center'}>
                    <Heading fontSize={'4xl'}>Register for an account</Heading>
                    <Text fontSize={'lg'} color={'gray.600'}>
                        to enjoy all of our cool features ✌️
                    </Text>
                </Stack>
                <Box
                    rounded={'lg'}
                    bg={useColorModeValue('white', 'gray.700')}
                    boxShadow={'lg'}
                    p={8}>
                    <Stack spacing={4}>
                        <CreateCustomerForm onSuccess={(token) => {
                            localStorage.setItem("access_token", token)
                            setCustomerFromToken()
                            navigate("/dashboard")
                        }}>

                        </CreateCustomerForm>
                    </Stack>
                </Box>
                <Link color={'blue.500'} href={"/"}>
                    Have an account? Login now.
                </Link>
            </Stack>
        </Flex>   
    )
}

export default Signup