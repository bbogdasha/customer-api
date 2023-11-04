import {
	AlertDialog,
    AlertDialogBody, 
	AlertDialogContent,
    AlertDialogFooter, 
	AlertDialogHeader, 
	AlertDialogOverlay,
	Heading,
	Avatar,
	Box,
	Center,
	Image,
	Flex,
	Text,
	Stack,
	Tag,
	useColorModeValue,
	Button,
	useDisclosure,
} from '@chakra-ui/react'
import { useRef } from 'react'
import { deleteCustomer } from '../services/client'
import { errorNotification, successNotification } from '../services/notification'
import UpdateCustomerDrawer from './UpdateCustomerDrawer'

export default function SocialProfileWithImage({id, name, email, age, gender, imageNumber, fetchCustomers}) {
	const randomUserGender = gender === "MALE" ? "men" : "women"
	const { isOpen, onOpen, onClose } = useDisclosure()
  	const cancelRef = useRef()

	return (
	<Center py={6} w={270} h={470}>
	  <Box
		maxW={'270px'}
		maxH={'470px'}
		w={'full'}
		h={'full'}
		bg={useColorModeValue('white', 'gray.800')}
		boxShadow={'2xl'}
		rounded={'md'}
		overflow={'hidden'}>
		<Tag borderRadius={"full"} position={"absolute"} mt={2} ml={2}>{id}</Tag>
		<Image
		  h={'120px'}
		  w={'full'}
		  src={
			'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
		  }
		  objectFit="cover"
		  alt="#"
		/>
		<Flex justify={'center'} mt={-12}>
		  <Avatar
			size={'xl'}
			src={
			  `https://randomuser.me/api/portraits/med/${randomUserGender}/${imageNumber}.jpg`
			}
			css={{
			  border: '2px solid white',
			}}
		  />
		</Flex>

		<Box p={6}>
		  <Stack spacing={2} align={'center'} mb={5}>
			<Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
			  {name}
			</Heading>
			<Text color={'gray.500'}>{email}</Text>
			<Text color={'gray.500'}>Age: {age} | {gender}</Text>
		  </Stack>
		</Box>
		<Stack direction={"row"} justify={"center"} spacing={8} mt={2}>
			<Stack>
				<UpdateCustomerDrawer 
					initialValues={{name, email, age}}
					customerId={id}
					fetchCustomers={fetchCustomers}
				>

				</UpdateCustomerDrawer>
			</Stack>
		
			<Stack>
				<Button 
					mt={2} 
					bg={"red.400"} 
					color={"white"} 
					rounded={"full"} 
					_hover={{
						transform: "translateY(-2px)",
						boxShadow: "lg"
					}}
					_focus={{
						bg: "green.500"
					}}
					onClick={onOpen}
				>
					Delete
				</Button>

				<AlertDialog
					isOpen={isOpen}
					leastDestructiveRef={cancelRef}
					onClose={onClose}
				>
					<AlertDialogOverlay>
						<AlertDialogContent>
							<AlertDialogHeader fontSize='lg' fontWeight='bold'>
								Delete Customer
							</AlertDialogHeader>

							<AlertDialogBody>
								Are you sure you want to delete {name}? You can't undo this action afterwards.
							</AlertDialogBody>

							<AlertDialogFooter>
								<Button ref={cancelRef} onClick={onClose}>
									Cancel
								</Button>
								<Button colorScheme='red' onClick={() => {
									deleteCustomer(id).then(res => {
										console.log(res)
										successNotification(
											"Customer deleted",
											`${name} was successfully deleted!`
										)
										fetchCustomers()
									}).catch(err => {
										console.log(err)
										errorNotification(
											err.response.data.error,
											err.response.data.message
										)
									}).finally(() => {
										onClose()
									})
								}} ml={3}>
									Delete
								</Button>
							</AlertDialogFooter>
						</AlertDialogContent>
					</AlertDialogOverlay>
				</AlertDialog>
			</Stack>
		</Stack>
	  </Box>
	</Center>
  )
}