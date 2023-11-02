import { 
    Wrap, 
    WrapItem, 
    Text, 
    Spinner 
} from '@chakra-ui/react'
import SidebarWithHeader from './components/shared/SideBar'
import SocialProfileWithImage from './components/Card'
import { useEffect, useState } from 'react'
import { getCustomers } from './services/client'

const App = () => {

    const [customers, setCustomers] = useState([])
    const [loading, setLoading] = useState(false)

    useEffect(() => {

        setLoading(true)

        getCustomers().then(res => {
            setCustomers(res.data)
        }).catch(err => {
            console.log(err)
        }).finally(() => {
            setLoading(false)
        })
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

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <Text>No customers available</Text>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <Wrap justify={"center"} spacing={"30px"}>
                {customers.map((customer, index) => (
                    <WrapItem key={index}>
                        <SocialProfileWithImage 
                            {...customer}
                            imageNumber={index}>
                            
                        </SocialProfileWithImage>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default App