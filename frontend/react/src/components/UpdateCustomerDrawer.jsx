import { 
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    useDisclosure
} from "@chakra-ui/react"
import UpdateCustomerForm from "./UpdateCustomerForm";

const CloseIcon = () => "x"

const UpdateCustomerDrawer = ({fetchCustomers, initialValues, customerId}) => {

    const { isOpen, onOpen, onClose } = useDisclosure()
    
    return <>
        <Button 
            mt={2} 
            bg={"blue.400"} 
            color={"white"} 
            rounded={"full"} 
            _hover={{
                transform: "translateY(-2px)",
                boxShadow: "lg"
            }}
            onClick={onOpen}
        >
            Update
        </Button>
        <Drawer isOpen={isOpen} onClose={onClose} size={"lg"}>
          <DrawerOverlay />
          <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>Update customer</DrawerHeader>
  
            <DrawerBody>
              <UpdateCustomerForm
                fetchCustomers={fetchCustomers}
                initialValues={initialValues}
                customerId={customerId}
              >

              </UpdateCustomerForm>
            </DrawerBody>
  
            <DrawerFooter>
            <Button 
                leftIcon={<CloseIcon/>}
                colorScheme="blue"
                onClick={onClose}
            >
                Close
            </Button>
            </DrawerFooter>
          </DrawerContent>
        </Drawer>
    </>
}

export default UpdateCustomerDrawer
