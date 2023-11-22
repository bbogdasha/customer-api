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
import CreateCustomerForm from "./CreateCustomerForm";

const AddIcon = () => "+"
const CloseIcon = () => "x"

const CreateCustomerDrawer = ({fetchCustomers}) => {

    const { isOpen, onOpen, onClose } = useDisclosure()
    
    return <>
        <Button 
            leftIcon={<AddIcon/>}
            colorScheme="teal"
            onClick={onOpen}
        >
            Create customer
        </Button>
        <Drawer isOpen={isOpen} onClose={onClose} size={"lg"}>
          <DrawerOverlay />
          <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>Create new customer</DrawerHeader>
  
            <DrawerBody>
              <CreateCustomerForm onSuccess={fetchCustomers}>

              </CreateCustomerForm>
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

export default CreateCustomerDrawer
