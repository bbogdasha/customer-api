import { 
    Wrap, 
    WrapItem, 
    Text, 
    Spinner 
} from '@chakra-ui/react'
import SidebarWithHeader from './components/shared/SideBar'
import SocialProfileWithImage from './components/customer/Card'
import { useEffect, useState } from 'react'
import { getCustomers } from './services/client'
import { errorNotification } from './services/notification'
import CreateCustomerDrawer from './components/customer/CreateCustomerDrawer'

const App = () => {

    const [customers, setCustomers] = useState([])
    const [loading, setLoading] = useState(false)
    const [err, setError] = useState("")

    const fetchCustomers = () => {
        setLoading(true)

        getCustomers().then(res => {
            setCustomers(res.data)
        }).catch(err => {
            setError(err.response.data.message)
            errorNotification(
                err.response.data.error,
                err.response.data.message
            )
        }).finally(() => {
            setLoading(false)
        })
    }

    useEffect(() => {
        fetchCustomers()
    }, [])

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        )
    }

    if (err) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}></CreateCustomerDrawer>
                <Text mt={5}>Ooops, there was an error</Text>
            </SidebarWithHeader>
        )
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}></CreateCustomerDrawer>
                <Text mt={5}>No customers available</Text>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer fetchCustomers={fetchCustomers} />
            <Wrap justify={"center"} spacing={"30px"}>
                {customers.map((customer, index) => (
                    <WrapItem key={index}>
                        <SocialProfileWithImage 
                            {...customer}
                            imageNumber={index}
                            fetchCustomers={fetchCustomers}
                        >

                        </SocialProfileWithImage>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default App
